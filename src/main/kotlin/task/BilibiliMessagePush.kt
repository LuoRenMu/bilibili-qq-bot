package cn.luorenmu.task

import cn.luorenmu.action.listenProcess.BilibiliMessageCollect
import cn.luorenmu.action.listenProcess.entity.BilibiliArticle
import cn.luorenmu.common.extensions.sendGroupMsgLimit
import cn.luorenmu.common.utils.BilibiliCacheUtils
import cn.luorenmu.common.utils.SETTING
import cn.luorenmu.config.entity.groups
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.common.utils.ShiroUtils
import com.mikuac.shiro.core.BotContainer
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

/**
 * @author LoMu
 * Date 2024.11.07 17:40
 */
val log = KotlinLogging.logger { }

@Component
class BilibiliMessagePush(
    private val botContainer: BotContainer,
    private val bilibiliMessageCollect: BilibiliMessageCollect,
) {

    private fun bot() = botContainer.robots.values.first()


    private fun groupList(retry: Int = 0): List<Long> {
        if (retry > 5) return groups
        var groupList: List<Long>
        try {
            groupList = bot().groupList.data
                .map { it.groupId }
                .filter { !SETTING.bannedGroupBilibiliPush.contains(it) }
                .toList()
        } catch (e: Exception) {
            log.warn { "获取群列表失败，尝试重新获取" }
            groupList = groupList(retry + 1)
        }
        groups = groupList
        return groupList
    }

    @Scheduled(cron = "0 */3 * * * *")
    fun timingPushArticle() {

        val listenList = SETTING.listenList

        listenList.forEach {
            val cache = BilibiliCacheUtils(it.uid)
            var lastArticles = listOf<BilibiliArticle>()

            if (BilibiliCacheUtils.exists(it.uid)) {
                lastArticles = bilibiliMessageCollect.articleMessageCollect(it.uid, 5).filter { bilibiliArticle ->
                    bilibiliArticle.id.toLong() > cache.readCache().lastArticle!!
                }
            } else {
                listOf(bilibiliMessageCollect.articleMessageCollect(it.uid, 5)
                    .maxBy { bilibiliArticle -> bilibiliArticle.id.toLong() })
            }

            lastArticles.forEach { lastArticle ->
                var forwardMessage: List<Map<String, Any>>? = null
                if (lastArticle.picUrl.size > 3) {
                    forwardMessage = buildForwardMessage(lastArticle.picUrl.map
                    { picUrl ->
                        MsgUtils.builder().img(picUrl).build()
                    })
                }
                groupList().forEach { groupId ->
                    bot().sendGroupMsgLimit(groupId, lastArticle.text)
                    forwardMessage?.let { fm ->
                        bot().sendGroupForwardMsg(groupId, fm)
                    } ?: run {
                        lastArticle.picUrl.forEach { picUrl ->
                            bot().sendGroupMsgLimit(groupId, MsgUtils.builder().img(picUrl).build())
                        }
                    }
                    TimeUnit.SECONDS.sleep(1)
                }
            }
            if (lastArticles.isNotEmpty()) {
                val maxId = lastArticles.maxBy { article -> article.id.toLong() }.id.toLong()
                cache.lastArticle = maxId
                cache.deleteThenWriteCache()
            }
        }


    }

    private fun buildForwardMessage(listof: List<String>): List<Map<String, Any>> {
        return ShiroUtils.generateForwardMsg(bot().selfId, "LomuBot", listof)
    }
}



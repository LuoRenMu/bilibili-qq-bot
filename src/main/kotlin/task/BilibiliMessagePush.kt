package cn.luorenmu.task

import cn.luorenmu.action.listenProcess.BilibiliMessageCollect
import cn.luorenmu.action.listenProcess.entity.BilibiliArticle
import cn.luorenmu.common.extensions.sendGroupMsgLimit
import cn.luorenmu.common.utils.BilibiliCacheUtils
import cn.luorenmu.common.utils.SETTING
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.common.utils.ShiroUtils
import com.mikuac.shiro.core.BotContainer
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

/**
 * @author LoMu
 * Date 2024.11.07 17:40
 */

@Component
class BilibiliMessagePush(
    private val botContainer: BotContainer,
    private val bilibiliMessageCollect: BilibiliMessageCollect,
) {

    private fun bot() = botContainer.robots.values.first()


    private fun groupList() = bot().groupList.data
        .map { it.groupId }
        .filter { !SETTING.bannedGroupBilibiliPush.contains(it) }
        .toList()

    @Scheduled(cron = "0 */3 * * * *")
    fun timingPushArticle() {

        val listenList = SETTING.listenList

        listenList.forEach {
            val cache = BilibiliCacheUtils(it.uid).readCache()
            var lastArticles = listOf<BilibiliArticle>()

            if (BilibiliCacheUtils.exists(it.uid)) {
                lastArticles = bilibiliMessageCollect.articleMessageCollect(it.uid, 5).filter { bilibiliArticle ->
                    bilibiliArticle.id.toLong() > cache.lastArticle!!
                }
            } else {
                listOf(bilibiliMessageCollect.articleMessageCollect(it.uid, 5)
                    .maxBy { bilibiliArticle -> bilibiliArticle.id.toLong() })
            }

            lastArticles.forEach { lastArticle ->
                if (lastArticle.picUrl.size > 3) {
                    val forwardMessage = buildForwardMessage(lastArticle.picUrl.map
                    { picUrl ->
                        MsgUtils.builder().img(picUrl).build()
                    })
                    groupList().forEach { groupId ->
                        bot().sendGroupMsgLimit(groupId, lastArticle.text)
                        bot().sendGroupForwardMsg(groupId, forwardMessage)
                        TimeUnit.SECONDS.sleep(1)
                    }
                }
            }
        }


    }

    private fun buildForwardMessage(listof: List<String>): List<Map<String, Any>> {
        return ShiroUtils.generateForwardMsg(bot().selfId, "LomuBot", listof)
    }
}



package cn.luorenmu.task

import cn.luorenmu.action.listenProcess.BilibiliMessageCollect
import cn.luorenmu.action.listenProcess.BilibiliRequestData
import cn.luorenmu.action.listenProcess.entity.BilibiliArticle
import cn.luorenmu.common.extensions.groupList
import cn.luorenmu.common.extensions.sendGroupListBilibiliarticle
import cn.luorenmu.common.utils.BilibiliCacheUtils
import cn.luorenmu.common.utils.SETTING
import com.mikuac.shiro.core.BotContainer
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2024.11.07 17:40
 */
val log = KotlinLogging.logger { }

@Component
class BilibiliMessagePush(
    private val botContainer: BotContainer,
    private val bilibiliMessageCollect: BilibiliMessageCollect,
    private val bilibiliRequestData: BilibiliRequestData,
) {

    private fun bot() = botContainer.robots.values.first()


    @Scheduled(cron = "0 */3 * * * *")
    fun timingPushArticle() {
        if (SETTING.listenList.isEmpty() || SETTING.listenList.firstOrNull { it.uid.isNotBlank() } == null) {
            return
        }
        val listenList = SETTING.listenList

        listenList.forEach {
            val cache = BilibiliCacheUtils(it.uid)

            val lastArticles: List<BilibiliArticle> = if (BilibiliCacheUtils.exists(it.uid)) {
                bilibiliMessageCollect.articleMessageCollect(it.uid, 5).filter { bilibiliArticle ->
                    bilibiliArticle.id.toLong() > cache.readCache().lastArticle!!
                }
            } else {
                listOf(bilibiliMessageCollect.articleMessageCollect(it.uid, 5)
                    .maxBy { bilibiliArticle -> bilibiliArticle.id.toLong() })
            }

            val groupList = it.groupBilibiliPush.ifEmpty {
                bot().groupList(5).filter { groupFilter ->
                    !SETTING.bannedGroupBilibiliPush.contains(groupFilter)
                }
            }
            //存在最新的动态
            lastArticles.forEach { lastArticle ->
                bot().sendGroupListBilibiliarticle(
                    lastArticle,
                    bilibiliRequestData,
                    groupList
                )

            }

            // 更新缓存
            if (lastArticles.isNotEmpty()) {
                val maxId = lastArticles.maxBy { article -> article.id.toLong() }.id.toLong()
                cache.lastArticle = maxId
                cache.deleteThenWriteCache()
            }
        }


    }


}



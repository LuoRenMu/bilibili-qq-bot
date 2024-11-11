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

            //存在最新的动态
            lastArticles.forEach { lastArticle ->
                bot().sendGroupListBilibiliarticle(
                    lastArticle,
                    bilibiliRequestData,
                    bot().groupList(5).filter { groupFilter ->
                        !SETTING.bannedGroupBilibiliPush.contains(groupFilter)
                    })

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



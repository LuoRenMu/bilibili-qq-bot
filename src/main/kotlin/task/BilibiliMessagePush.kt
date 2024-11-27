package cn.luorenmu.task

import cn.luorenmu.action.listenProcess.BilibiliMessageCollect
import cn.luorenmu.action.listenProcess.BilibiliRequestData
import cn.luorenmu.action.listenProcess.entity.BilibiliArticle
import cn.luorenmu.common.extensions.groupList
import cn.luorenmu.common.extensions.sendGroupListBilibiliarticle
import cn.luorenmu.common.extensions.sendGroupMessageConvert
import cn.luorenmu.common.utils.file.BilibiliCacheUtils
import cn.luorenmu.common.utils.file.SETTING
import cn.luorenmu.config.entity.MessageConvert
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.core.BotContainer
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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
    private val bilibiliRequestData: BilibiliRequestData,
) {

    private fun bot() = botContainer.robots.values.first()


    fun pushLive(cache: BilibiliCacheUtils, uid: String, upName: String, groupList: List<Long>) {
        val uidLiveRoom = bilibiliRequestData.uidLiveRoom(uid)
        uidLiveRoom?.let { room ->
            if (room.data.liveStatus == 1 && room.data.roomStatus == 1 && room.data.roundStatus == 0) {
                //缓存中没有开播
                if (cache.onLive == null || cache.onLive == false) {
                    cache.onLive = true
                    val map = mutableMapOf(
                        MessageConvert.FORMAT.UP_NAME to upName,
                        MessageConvert.FORMAT.LIVE_COVER to
                                MsgUtils.builder().img(room.data.cover).build(),
                        MessageConvert.FORMAT.LIVE_TITLE to room.data.title,
                    )
                    groupList.forEach { groupId ->
                        bot().sendGroupMessageConvert(groupId, MessageConvert.ID.LIVE_PUSH, map)
                    }
                }
            } else {
                cache.onLive = false
            }
        }
    }

    fun pushArticle(lastArticles: List<BilibiliArticle>, groupList: List<Long>) {
        //存在最新动态 发送消息
        lastArticles.forEach { lastArticle ->
            bot().sendGroupListBilibiliarticle(
                lastArticle,
                bilibiliRequestData,
                groupList
            )

        }
    }

    @Scheduled(cron = "0 */3 * * * *")
    fun timingPush() {
        if (SETTING.listenList.isEmpty() || SETTING.listenList.firstOrNull { it.uid.isNotBlank() } == null) {
            return
        }
        val listenList = SETTING.listenList

        listenList.forEach {
            val cache = BilibiliCacheUtils(it.uid)
            val articleMessageCollect = bilibiliMessageCollect.articleMessageCollect(it.uid, 5)
            val lastArticles: List<BilibiliArticle> = if (BilibiliCacheUtils.exists(it.uid)) {
                articleMessageCollect.filter { bilibiliArticle ->
                    bilibiliArticle.id.toLong() > cache.readCache().lastArticle!!
                }
            } else {
                listOf(articleMessageCollect
                    .maxBy { bilibiliArticle -> bilibiliArticle.id.toLong() })
            }

            val upName = articleMessageCollect.first().name
            val groupList = it.groupBilibiliPush.ifEmpty {
                bot().groupList(5).filter { groupFilter ->
                    !SETTING.bannedGroupBilibiliPush.contains(groupFilter)
                }
            }

            pushArticle(lastArticles, groupList)
            pushLive(cache, it.uid, upName, groupList)

            // 更新缓存
            if (lastArticles.isNotEmpty()) {
                val maxId = lastArticles.maxBy { article -> article.id.toLong() }.id.toLong()
                cache.lastArticle = maxId
                cache.deleteThenWriteCache()
            }
            TimeUnit.SECONDS.sleep(2)

        }
        log.info {
            "Next execution at ${
                LocalDateTime.now().plusMinutes(3).format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            }"
        }

    }


}



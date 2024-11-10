package cn.luorenmu.action.listenProcess

import cn.luorenmu.common.extensions.sendGroupMsgLimit
import cn.luorenmu.common.utils.MatcherData
import cn.luorenmu.common.utils.getVideoPath
import cn.luorenmu.config.external.BilibiliConfig
import cn.luorenmu.entiy.Request
import cn.luorenmu.listen.log
import cn.luorenmu.request.RequestController
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.core.Bot
import org.springframework.stereotype.Component
import java.io.File
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import kotlin.jvm.optionals.getOrNull

/**
 * @author LoMu
 * Date 2024.09.12 21:13
 */
@Component
class BilibiliEventListen(
    private val bilibiliRequestData: BilibiliRequestData,
    private val config: BilibiliConfig,
) {
    private val bilibiliVideoLongLink = "BV[a-zA-Z0-9]+"
    private val bilibiliVideoShortLink = "https://b23.tv/([a-zA-Z0-9]+)"

    companion object {
        val bilibiliExistVideo = ConcurrentHashMap<String, LocalDateTime>()
    }

    fun process(bot: Bot, groupId: Long, message: String) {
        val correctMsg = message.replace("\\", "")
        findBilibiliLinkBvid(correctMsg)?.let { bvid ->
            val videoPath = getVideoPath("bilibili/$bvid.flv")
            val videoPathCQ = MsgUtils.builder().video(videoPath, "").build()

            bilibiliRequestData.bvidToCid(bvid)?.let { videoInfo ->
                var videoInfoStr = ""
                videoInfo.firstFrame?.let {
                    videoInfoStr += MsgUtils.builder().img(it).build()
                }
                videoInfoStr += "${videoInfo.part} https://www.bilibili.com/video/$bvid"

                bot.sendGroupMsgLimit(
                    groupId, videoInfoStr
                )

                downloadVideo(bvid, videoInfo.cid, videoPath)?.let { downloadVideo ->
                    if (downloadVideo) {
                        if (config.delete != 0) {
                            bilibiliExistVideo[videoPath] = LocalDateTime.now()
                        }
                        bot.sendGroupMsgLimit(groupId, videoPathCQ)
                    }
                } ?: run {
                    bot.sendGroupMsgLimit(groupId, "视频下载失败")
                }


            }
        }

    }


    fun findBilibiliLinkBvid(message: String): String? {

        if (message.contains(bilibiliVideoLongLink.toRegex())) {
            val bvid = MatcherData.matcherIndexStr(message, bilibiliVideoLongLink, 0).getOrNull()
            bvid?.let {
                if (bvid.length == 12) {
                    return bvid
                }
            }
            return null
        }
        if (message.contains(bilibiliVideoShortLink.toRegex())) {
            // short link to long link
            val shortLink = MatcherData.matcherStr(message, bilibiliVideoShortLink, 1, "").getOrNull()

            val respBody = RequestController(Request.RequestDetailed().apply {
                url = shortLink!!
                method = "GET"
            }).request().body()

            return MatcherData.matcherIndexStr(respBody, bilibiliVideoLongLink, 0).getOrNull()
        }

        return null
    }


    /**
     *  @param bvid (bv号)
     *  @param outputPath video save local path need file name and video type (default type is flv)
     *  @return null is video download failed  if video too large (limit length $minute minute) return false else true
     */
    fun downloadVideo(bvid: String, cid: Long, outputPath: String): Boolean? {
        synchronized(bilibiliVideoLongLink) {
            if (File(outputPath).exists()) {
                return true
            }
        }

        try {
            val videoInfos = bilibiliRequestData.getVideoInfo(bvid, cid)
            videoInfos?.let {
                val minute = videoInfos.timelength / 1000 / 60
                if (minute > config.limit) {
                    return false
                }
                videoInfos.durl.firstOrNull()?.let { videoInfo ->
                    if (bilibiliRequestData.downloadVideo(videoInfo.url, outputPath)) {
                        return true
                    }
                }
            }
        } catch (e: Exception) {
            log.error { e.stackTraceToString() }
        }
        return null
    }

}
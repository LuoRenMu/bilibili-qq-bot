package cn.luorenmu.action.listenProcess

import cn.luorenmu.action.request.BilibiliRequestData
import cn.luorenmu.common.extensions.sendGroupMsgLimit
import cn.luorenmu.common.utils.MatcherData
import cn.luorenmu.common.utils.file.SETTING
import cn.luorenmu.entiy.Request
import cn.luorenmu.request.RequestController
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.core.Bot
import org.springframework.stereotype.Component
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
) {
    private val bilibiliVideoLongLink = "BV[a-zA-Z0-9]+"
    private val bilibiliVideoShortLink = "https://b23.tv/([a-zA-Z0-9]+)"

    companion object {
        val bilibiliExistVideo = ConcurrentHashMap<String, LocalDateTime>()
    }

    fun process(bot: Bot, groupId: Long, message: String) {
        val correctMsg = message.replace("\\", "")
        findBilibiliLinkBvid(correctMsg)?.let { bvid ->

            bilibiliRequestData.bvidToCid(bvid)?.let { videoInfo ->
                var videoInfoStr = ""
                videoInfo.firstFrame?.let {
                    videoInfoStr += MsgUtils.builder().img(it).build()
                }
                videoInfoStr += "${videoInfo.part} https://www.bilibili.com/video/$bvid"

                bot.sendGroupMsgLimit(
                    groupId, videoInfoStr
                )

                bilibiliRequestData.downloadVideo(bvid, videoInfo.cid)?.let { videoPath ->
                    if (videoPath.isNotBlank()) {
                        if (SETTING.bilibiliDelete != 0) {
                            bilibiliExistVideo[videoPath] = LocalDateTime.now()
                        }
                        bot.sendGroupMsgLimit(groupId, MsgUtils.builder().video(videoPath, "").build())
                    }
                } ?: run {
                    bot.sendGroupMsgLimit(groupId, "视频下载失败")
                }
            } ?: run {
                bot.sendGroupMsgLimit(groupId, "获取视频信息视频(视频不存在)")
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


}
package cn.luorenmu.action.request

import cn.luorenmu.action.request.entity.response.*
import cn.luorenmu.common.utils.file.SETTING
import cn.luorenmu.common.utils.getVideoPath
import cn.luorenmu.entiy.Request.RequestParam
import cn.luorenmu.listen.log
import cn.luorenmu.request.RequestController
import com.alibaba.fastjson2.to
import org.springframework.stereotype.Component
import java.io.File

/**
 * @author LoMu
 * Date 2024.09.12 21:19
 */

/**
 * 0：成功
 * -400：请求错误
 * -404：无视频
 */
@Component
class BilibiliRequestData : RequestData() {

    /**
     *  @param bvid (bv号)
     *  @return null is video download failed  if video too large (limit length $minute minute) return false else true
     */
    fun downloadVideo(bvid: String, cid: Long): String? {
        val outputPath = getVideoPath("bilibili/$bvid.flv")
        synchronized(BilibiliRequestData::class) {
            if (File(outputPath).exists()) {
                return outputPath
            }
        }

        try {
            val videoInfos = getVideoInfo(bvid, cid)
            videoInfos?.let {
                val minute = videoInfos.timelength / 1000 / 60
                if (minute > SETTING.bilbiliLimit) {
                    return ""
                }
                videoInfos.durl.firstOrNull()?.let { videoInfo ->
                    val header = RequestParam("referer", "https://www.bilibili.com")
                    if (downloadStream(videoInfo.url, outputPath, mutableListOf(header))) {
                        return outputPath
                    }
                }
            }
        } catch (e: Exception) {
            log.error { e.stackTraceToString() }
        }
        return null
    }


    fun getVideoInfo(bvid: String, cid: Long): BilibiliVideoInfoData? {
        val requestController = RequestController("bilibili_request.get_video_info")
        requestController.setWaitTime(1L)
        requestController.replaceUrl("bvid", bvid)
        requestController.replaceUrl("cid", cid.toString())
        val resp = requestController.request()
        resp?.let {
            val body = it.body()
            val result = body.to<BilibiliVideoInfo>()
            if (result.code == 0) {
                return result.data.firstOrNull()
            }
        }
        return null
    }


    fun bvidToCid(bvid: String): BilibiliPageInfoData? {
        val requestController = RequestController("bilibili_request.bvid_to_cid")
        requestController.replaceUrl("bvid", bvid)
        val resp = requestController.request()
        resp?.let {
            val result = it.body().to<BilibiliPageListInfo>()
            return result.data.firstOrNull()
        }
        return null
    }

    fun space(uid: String): BilibiliSpace? {
        val requestController = RequestController("bilibili_request.get_space_info")
        requestController.replaceUrl("uid", uid)
        val resp = requestController.request()
        resp?.let {
            return it.body().to<BilibiliSpace>()
        }
        return null
    }

    fun uidLiveRoom(uid: String): BilibiliLiveRoomInfo? {
        val requestController = RequestController("bilibili_request.uid_get_live_room")
        requestController.replaceUrl("uid", uid)
        val resp = requestController.request()
        resp?.let {
            return it.body().to<BilibiliLiveRoomInfo>()
        }
        return null
    }

}
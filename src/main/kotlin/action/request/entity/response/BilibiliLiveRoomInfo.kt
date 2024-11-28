package cn.luorenmu.action.request.entity.response

import com.alibaba.fastjson2.annotation.JSONField

/**
 * @author LoMu
 * Date 2024.11.24 18:23
 */
data class BilibiliLiveRoomInfo(
    val code: Int,
    val message: String,
    val ttl: Int,
    val data: BilibiliLiveRoomInfoData,

    )

data class BilibiliLiveRoomInfoData(
    // 直播间状态 0：无房间  1：有房间
    val roomStatus: Int,
    // 轮播状态 0：无轮播  1：轮播
    val roundStatus: Int,
    // 直播状态 0：未开播  1：开播
    val liveStatus: Int,
    val url: String,
    val title: String,
    val cover: String,
    // 直播间人气
    val online: Int,
    @JSONField(name = "roomid")
    val roomId: String,
    @JSONField(name = "broadcast_type")
    val broadcastType: Int,
    @JSONField(name = "online_hidden")
    val onlineHidden: Int,
)

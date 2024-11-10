package cn.luorenmu.config.entity

import com.alibaba.fastjson2.annotation.JSONField

/**
 * @author LoMu
 * Date 2024.11.03 16:41
 */
data class LoMuBotSetting(
    @JSONField(name = "bot_owner")
    var botOwner: Long = 0L,
    @JSONField(name = "group_bvid_listen")
    var groupBvidListen: Boolean = true,
    @JSONField(name = "bilibili_video_delete_timing")
    var bilibiliDelete: Int = 3,
    @JSONField(name = "bilibili_video_length_limit")
    var bilbiliLimit: Int = 5,
    @JSONField(name = "banned_group_bvid_listen")
    var bannedGroupBvidListen: List<Long> = arrayListOf(0L),
    @JSONField(name = "banned_group_bilibili_push")
    var bannedGroupBilibiliPush: List<Long> = arrayListOf(0L),
    @JSONField(name = "listen_list")
    var listenList: List<ListenSetting> = arrayListOf(ListenSetting("")),
)

data class ListenSetting(
    var uid: String,
    @JSONField(name = "live_broadcast")
    var liveBroadcast: Boolean = false,
)
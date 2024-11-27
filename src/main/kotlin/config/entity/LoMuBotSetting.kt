package cn.luorenmu.config.entity

import com.alibaba.fastjson2.annotation.JSONField

/**
 * @author LoMu
 * Date 2024.11.03 16:41
 */
data class LoMuBotSetting(
    @JSONField(name = "bot_owner")
    var botOwner: Long = 0L,
    @JSONField(name = "image_forward")
    var imageForward: Int = 3,
    @JSONField(name = "text_forward")
    var textForward: Int = 500,
    @JSONField(name = "group_bvid_listen")
    var groupBvidListen: Boolean = true,
    @JSONField(name = "bilibili_video_delete_timing")
    var bilibiliDelete: Int = 3,
    @JSONField(name = "bilibili_video_length_limit")
    var bilbiliLimit: Int = 5,
    @JSONField(name = "banned_group_bvid_listen")
    var bannedGroupBvidListen: MutableList<Long> = mutableListOf(0L),
    @JSONField(name = "banned_group_bilibili_push")
    var bannedGroupBilibiliPush: MutableList<Long> = mutableListOf(0L),
    @JSONField(name = "listen_list")
    var listenList: MutableList<ListenSetting> = mutableListOf(ListenSetting("")),
    @JSONField(name = "leave_group_owner_not_exists")
    var leaveGroupOwner: Boolean = false,
    @JSONField(name = "ignore_private_messages")
    var ignorePrivateMessages: Boolean = true,
)

data class ListenSetting(
    var uid: String,
    @JSONField(name = "live_broadcast")
    var liveBroadcast: Boolean = false,
    @JSONField(name = "group_bilibili_push")
    var groupBilibiliPush: MutableList<Long> = mutableListOf(),
)
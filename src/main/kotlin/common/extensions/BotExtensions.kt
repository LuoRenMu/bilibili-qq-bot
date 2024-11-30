package cn.luorenmu.common.extensions

import cn.luorenmu.action.listenProcess.entity.BilibiliArticle
import cn.luorenmu.action.request.BilibiliRequestData
import cn.luorenmu.common.extensions.entity.RecentlyMessageQueue
import cn.luorenmu.common.extensions.entity.SelfSendMsg
import cn.luorenmu.common.utils.file.MessageConvertFileUtils.MESSAGE_CONVERT
import cn.luorenmu.common.utils.file.SettingFileUtils.SETTING
import cn.luorenmu.config.entity.MessageConvert
import cn.luorenmu.config.entity.QQ_GROUPS
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.common.utils.ShiroUtils
import com.mikuac.shiro.core.Bot
import com.mikuac.shiro.dto.action.common.ActionData
import com.mikuac.shiro.dto.action.common.MsgId
import io.github.oshai.kotlinlogging.KotlinLogging

/**
 * @author LoMu
 * Date 2024.07.28 18:03
 */

private val selfRecentlySendMessage: RecentlyMessageQueue<SelfSendMsg> = RecentlyMessageQueue(3)
private val log = KotlinLogging.logger { }

/**
 *  bool 表示消息队列是否存在该消息
 */
fun selfRecentlySent(id: Long, message: String): Boolean {
    synchronized(selfRecentlySendMessage) {
        val selfSendMsgs = selfRecentlySendMessage.map[id]
        selfSendMsgs?.forEach {
            if (it.message == message) {
                return true
            }
        }
        return false
    }
}

fun Bot.sendGroupMsg(groupId: Long, message: String): ActionData<MsgId>? {
    if (message.length > SETTING.textForward) {
        if (!message.contains("[CQ:,")) {
            val chunked = message.chunked(SETTING.textForward)
            val buildForwardMessage = buildForwardMessage(this.selfId, chunked.toMutableList())
            return sendGroupForwardMsg(groupId, buildForwardMessage)
        }
    }

    return this.sendGroupMsg(groupId, message, false)
}

fun Bot.sendGroupMsgLimit(groupId: Long, message: String): Boolean {
    if (!SETTING.messageLimitList) {
        this.sendGroupMsg(groupId, message, false)
        return true
    }
    return sendMsgLimit(groupId, message) {
        sendGroupMsg(groupId, message)
    }
}

@Suppress("unused")
fun Bot.sendGroupMsgLimit(groupId: Long, message: String, msgLimit: String): Boolean {
    return sendMsgLimit(groupId, message, msgLimit) {
        sendGroupMsg(groupId, message)
    }
}

@Suppress("unused")
fun Bot.sendPrivateMsgLimit(id: Long, message: String): Boolean {
    sendMsgLimit(id, message) {
        this.sendPrivateMsg(id, message)
    }
    return true
}


fun Bot.sendPrivateMsg(id: Long, message: String): ActionData<MsgId>? {
    if (message.length > SETTING.textForward) {
        val chunked = message.chunked(SETTING.textForward)
        val buildForwardMessage = buildForwardMessage(this.selfId, chunked.toMutableList())
        return sendPrivateForwardMsg(id, buildForwardMessage)
    }
    return this.sendPrivateMsg(id, message, false)
}

@Suppress("unused", "UnusedReceiverParameter")
fun Bot.addMsgLimit(id: Long, message: String, msgLimit: String = "none") {
    synchronized(selfRecentlySendMessage) {
        var msgLimit1 = message
        if (msgLimit != "none") {
            msgLimit1 = msgLimit
        }
        if (selfRecentlySent(id, msgLimit1)) {
            return
        }

        selfRecentlySendMessage.addMessageToQueue(id, SelfSendMsg(msgLimit1))
    }
}

/**
 * msgLimit 用于作为限制的消息  (图片消息)
 * 由于图片的名称一样 但是URL不一样 所以需要截取名称作为限制条件
 */
@Suppress("UnusedReceiverParameter")
private fun Bot.sendMsgLimit(
    id: Long,
    message: String,
    msgLimit: String = "none",
    send: () -> ActionData<MsgId>?,
): Boolean {
    synchronized(selfRecentlySendMessage) {
        var msgLimit1 = message
        if (msgLimit != "none") {
            msgLimit1 = msgLimit
        }
        if (selfRecentlySent(id, msgLimit1)) {
            return false
        }
        val sendMsg = send()
        val selfSendMsg: SelfSendMsg = if (sendMsg != null && sendMsg.data != null) {
            SelfSendMsg(sendMsg.data.messageId.toLong(), msgLimit1)
        } else {
            SelfSendMsg(msgLimit1)
        }
        selfRecentlySendMessage.addMessageToQueue(id, selfSendMsg)
        log.info { "send message $id -> $message" }
        return true
    }
}


fun Bot.sendGroupBilibiliarticle(
    lastArticle: BilibiliArticle,
    bilibiliRequestData: BilibiliRequestData,
    groupId: Long,
) {
    sendBilibiliArticle(lastArticle, bilibiliRequestData, arrayListOf(groupId)) { group, message ->
        sendGroupMsgLimit(group, message)
    }
}

fun Bot.sendGroupListBilibiliarticle(
    lastArticle: BilibiliArticle,
    bilibiliRequestData: BilibiliRequestData,
    groupList: List<Long>,
) {
    sendBilibiliArticle(lastArticle, bilibiliRequestData, groupList) { groupId, message ->
        sendGroupMsgLimit(groupId, message)
    }
}

fun Bot.sendGroupMessageConvert(groupId: Long, id: String, map: MutableMap<String, String>): Boolean {
    val build = MESSAGE_CONVERT.replace(id, map).build()
    return this.sendGroupMsgLimit(groupId, build)
}


fun Bot.sendBilibiliArticle(
    lastArticle: BilibiliArticle,
    bilibiliRequestData: BilibiliRequestData,
    groupList: List<Long>,
    send: (Long, String) -> Boolean,
) {
    var forwardMessage: List<Map<String, Any>> = mutableListOf()

    //图片数量大于3通过转发发送
    if (lastArticle.picUrl.size > SETTING.imageForward) {
        forwardMessage = buildForwardMessage(this.selfId, lastArticle.picUrl.map { picUrl ->
            MsgUtils.builder().img(picUrl).build()
        }.toMutableList())
    }

    //视频下载
    val path = lastArticle.videoBvid?.let { videoBvid ->
        bilibiliRequestData.bvidToCid(videoBvid)?.let { cid ->
            bilibiliRequestData.downloadVideo(videoBvid, cid.cid)
        }
    }


    //群推送
    groupList.forEach { groupId ->
        // 发送文本内容
        send(
            groupId,
            MESSAGE_CONVERT.replace(
                MessageConvert.ID.BILIBILI_ARTICLE, MessageConvert.FORMAT.UP_NAME, lastArticle.name
            ).replace(
                MessageConvert.FORMAT.ORIGIN_TEXT, lastArticle.text
            ).build()
        )

        // 转发内容
        if (forwardMessage.isNotEmpty()) {
            this.sendGroupForwardMsg(groupId, forwardMessage)
        } else {
            // 不转发图片
            lastArticle.picUrl.forEach { picUrl ->
                send(groupId, MsgUtils.builder().img(picUrl).build())
            }
        }

        // 视频
        if (path?.isNotBlank() == true) {
            send(
                groupId, MsgUtils.builder().video(path, lastArticle.picUrl.first()).build()
            )
        }
    }

}

fun Bot.groupList(retry: Int = 0): List<Long> {
    if (retry > 5) return QQ_GROUPS
    var groupList: List<Long>
    try {
        groupList = this.groupList.data.map { it.groupId }.toList()
    } catch (e: Exception) {
        cn.luorenmu.task.log.warn { "get group list failed ,retry get" }
        groupList = groupList(retry + 1)
    }
    QQ_GROUPS = groupList
    return groupList
}

fun buildForwardMessage(id: Long, list: MutableList<String>): List<Map<String, Any>> {
    for ((index, str) in list.withIndex()) {
        list[index] = MESSAGE_CONVERT.replace(MessageConvert.ID.FORWARD_IMAGE, MessageConvert.FORMAT.ORIGIN_IMAGE, str)
            .replace(MessageConvert.ID.FORWARD_TEXT, MessageConvert.FORMAT.ORIGIN_TEXT, str).build()
    }
    return ShiroUtils.generateForwardMsg(id, "1", list)
}
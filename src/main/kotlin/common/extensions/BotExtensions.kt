package cn.luorenmu.common.extensions

import cn.luorenmu.entiy.RecentlyMessageQueue
import cn.luorenmu.entiy.SelfSendMsg
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

fun Bot.sendGroupMsgLimit(groupId: Long, message: String): Boolean {
    return sendMsgLimit(groupId, message) {
        this.sendGroupMsg(groupId, message, false)
    }
}

fun Bot.sendGroupMsgLimit(groupId: Long, message: String, msgLimit: String): Boolean {
    return sendMsgLimit(groupId, message, msgLimit) {
        this.sendGroupMsg(groupId, message, false)
    }
}

fun Bot.sendPrivateMsgLimit(id: Long, message: String) {
    sendMsgLimit(id, message) {
        this.sendPrivateMsg(id, message, false)
    }
}


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

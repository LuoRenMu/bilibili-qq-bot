package cn.luorenmu.command.entity

/**
 * @author LoMu
 * Date 2024.11.17 19:50
 */
data class CommandSender(
    var groupOrSenderId: Long,
    var senderName: String,
    var senderId: Long,
    var role: BotRole,
    var messageId: Int,
    var message: String,
    var messageType: MessageType,
    // unlimited is true if disregard role limit
    var unlimited: Boolean = false,
)

enum class MessageType{
    PRIVATE,GROUP
}
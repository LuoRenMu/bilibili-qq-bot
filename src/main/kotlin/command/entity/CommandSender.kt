package cn.luorenmu.command.entity

/**
 * @author LoMu
 * Date 2024.11.17 19:50
 */
data class CommandSender (
    var groupOrSenderId: Long,
    var senderName: String,
    var senderId: Long,
    var role : BotRole,
    var message: String,
    // unlimited is true if disregard role limit
    var unlimited : Boolean = false
)
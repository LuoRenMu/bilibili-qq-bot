package cn.luorenmu.listen

import cn.luorenmu.command.CommandAllocator
import cn.luorenmu.command.CustomizeCommandAllocator
import cn.luorenmu.command.entity.BotRole
import cn.luorenmu.command.entity.CommandSender
import cn.luorenmu.command.entity.MessageType
import cn.luorenmu.common.extensions.sendPrivateMsg
import cn.luorenmu.common.utils.file.SETTING
import com.mikuac.shiro.annotation.PrivateMessageHandler
import com.mikuac.shiro.annotation.PrivateMsgDeleteNoticeHandler
import com.mikuac.shiro.annotation.common.Shiro
import com.mikuac.shiro.core.Bot
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent
import com.mikuac.shiro.dto.event.notice.PrivateMsgDeleteNoticeEvent
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2024.07.05 7:58
 */

@Component
@Shiro
class PrivateEvenListen(
    val commandAllocator: CommandAllocator,
    val customizeCommandAllocator: CustomizeCommandAllocator,
) {

    @PrivateMessageHandler
    fun privateMessageHandler(bot: Bot, privateMessage: PrivateMessageEvent) {
        val sender = privateMessage.privateSender
        if (!(SETTING.ignorePrivateMessages && sender.userId == SETTING.botOwner)) {
            return
        }
        val role = if (SETTING.botOwner == sender.userId) BotRole.OWNER else BotRole.Member
        val commandSender = CommandSender(
            privateMessage.userId,
            sender.nickname,
            sender.userId,
            role,
            privateMessage.messageId,
            privateMessage.message,
            MessageType.PRIVATE
        )

        customizeCommandAllocator.allocatorAfterProcess(commandSender)?.let {
            it.forEach { returnMessage ->
                bot.sendPrivateMsg(sender.userId, returnMessage)
            }
        }

        commandAllocator.allocator(commandSender)?.let {
            bot.sendPrivateMsg(sender.userId, it, false)
        }
    }

    @PrivateMsgDeleteNoticeHandler
    fun privateDeleteHandler(bot: Bot, privateMessage: PrivateMsgDeleteNoticeEvent) {

    }
}
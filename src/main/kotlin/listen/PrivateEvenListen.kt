package cn.luorenmu.listen

import cn.luorenmu.command.CommandAllocator
import cn.luorenmu.command.entity.BotRole
import cn.luorenmu.command.entity.CommandSender
import cn.luorenmu.common.utils.COMMAND
import cn.luorenmu.common.utils.SETTING
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
) {

    @PrivateMessageHandler
    fun privateMessageHandler(bot: Bot, privateMessage: PrivateMessageEvent) {
        val sender = privateMessage.privateSender
        val role = if (SETTING.botOwner == sender.userId) BotRole.OWNER else BotRole.Member
        log.info { COMMAND }
        commandAllocator.allocator(
            CommandSender(
                privateMessage.userId,
                sender.nickname,
                sender.userId,
                role,
                privateMessage.message
            )
        )?.let {
            bot.sendPrivateMsg(sender.userId, it, false)
        }
    }

    @PrivateMsgDeleteNoticeHandler
    fun privateDeleteHandler(bot: Bot, privateMessage: PrivateMsgDeleteNoticeEvent) {

    }
}
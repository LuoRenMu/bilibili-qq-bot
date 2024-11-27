package cn.luorenmu.listen

import cn.luorenmu.action.listenProcess.BilibiliEventListen
import cn.luorenmu.command.CommandAllocator
import cn.luorenmu.command.CustomizeCommandAllocator
import cn.luorenmu.command.entity.BotRole
import cn.luorenmu.command.entity.CommandSender
import cn.luorenmu.common.extensions.sendGroupMsgLimit
import cn.luorenmu.common.utils.file.SETTING
import com.mikuac.shiro.annotation.GroupMessageHandler
import com.mikuac.shiro.annotation.common.Shiro
import com.mikuac.shiro.core.Bot
import com.mikuac.shiro.dto.event.message.GroupMessageEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component


/**
 * @author LoMu
 * Date 2024.07.04 10:22
 */

val log = KotlinLogging.logger { }

@Component
@Shiro
class GroupEventListen(
    private val commandAllocator: CommandAllocator,
    private val bilibiliEventListen: BilibiliEventListen,
    private val customizeCommandAllocator: CustomizeCommandAllocator,
) {
    @GroupMessageHandler
    fun groupMsgListen(bot: Bot, groupMessageEvent: GroupMessageEvent) {
        val groupId = groupMessageEvent.groupId
        val message = groupMessageEvent.message

        if (SETTING.groupBvidListen) {
            if (!SETTING.bannedGroupBvidListen.contains(groupId)) {
                bilibiliEventListen.process(bot, groupId, message)
            }
        }
        val sender = groupMessageEvent.sender
        val role = when (sender.role) {
            "admin" -> BotRole.GroupAdmin
            "owner" -> BotRole.GroupOwner
            else -> BotRole.Member

        }
        val commandSender = CommandSender(groupId, sender.nickname, sender.userId, role, message, false)
        customizeCommandAllocator.allocator(commandSender)?.let {
            bot.sendGroupMsgLimit(groupId, it)
        }
        commandAllocator.allocator(commandSender)?.let {
            bot.sendGroupMsgLimit(groupId, it)
        }
    }
}
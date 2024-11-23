package cn.luorenmu.command

import cn.luorenmu.command.entity.BotRole
import cn.luorenmu.command.entity.CommandId
import cn.luorenmu.command.entity.CommandSender
import cn.luorenmu.common.utils.COMMAND
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2024.11.17 19:47
 */
@Component
class CommandAllocator(
    val commandProcess: CommandProcess,
) {

    /**
     * @param configRoleStr command config role
     * @param senderRole
     * @return true if senderRole gte config role else false
     */
    fun rolePermissions(configRoleStr: String, senderRole: BotRole): Boolean {
        return BotRole.convert(configRoleStr).roleNumber <= senderRole.roleNumber
    }

    fun commandMatcher(commandMessage: String, sender: CommandSender): Boolean {
        if (sender.message.contains(commandMessage.toRegex())) {
            sender.message = sender.message.replace(commandMessage.toRegex(), "")
            return true
        }
        return false
    }

    fun allocator(sender: CommandSender): String? {
        return COMMAND.commandList.firstOrNull { commandMatcher(it.command, sender) }?.let { command ->
            if (!rolePermissions(command.role, sender.role)) {
                command.returnMessage
            }
            return when (command.commandId) {
                CommandId.REFRESH_CONFIG.id -> {
                    return@let commandProcess.refreshConfig()
                }

                CommandId.GET_LAST_ARTICLE.id -> {
                    return@let commandProcess.refreshConfig()

                }

                else -> {
                    command.returnMessage
                }
            }
        }
    }

}
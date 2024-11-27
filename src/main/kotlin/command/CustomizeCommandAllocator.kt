package cn.luorenmu.command

import cn.luorenmu.command.entity.BotRole
import cn.luorenmu.command.entity.CommandSender
import cn.luorenmu.common.utils.file.CUSTOMIZE_COMMAND
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2024.11.27 12:17
 */
@Component
class CustomizeCommandAllocator {
    private val log = KotlinLogging.logger {}

    fun rolePermissions(configRoleStr: String, senderRole: BotRole): Boolean {
        return BotRole.convert(configRoleStr).roleNumber <= senderRole.roleNumber
    }

    fun commandMatcher(commandMessage: String, sender: CommandSender): Boolean {
        return sender.message.contains(commandMessage.toRegex())
    }


    fun allocator(sender: CommandSender): String? {
        val customizeCommandList = CUSTOMIZE_COMMAND.customizeCommandList.filter {
            commandMatcher(it.command, sender)
        }
        var limitMessage: String? = null

        if (customizeCommandList.isNotEmpty()) {
            // 群筛选 -> 指定发送者筛选 都不存在 角色筛选
            val groupLimitCustomizeCommandList = customizeCommandList.filter { it.groupList.isNotEmpty() }


            if (groupLimitCustomizeCommandList.isNotEmpty()) {
                groupLimitCustomizeCommandList.firstOrNull { it.groupList.contains(sender.groupOrSenderId) }?.let {
                    groupLimitCustomizeCommandList.filter { it.senderList.contains(sender.senderId) }.randomOrNull()
                        ?.let {
                            return it.returnMessage
                        } ?: run {
                        groupLimitCustomizeCommandList.filter { rolePermissions(it.role, sender.role) }.randomOrNull()
                            ?.let {
                                return it.returnMessage
                            } ?: run {
                            limitMessage = groupLimitCustomizeCommandList.random().permissionsMessage
                        }

                    }
                }
            }

            // 发送者筛选
            val senderLimit =
                customizeCommandList.filter { it.senderList.isNotEmpty() && it.groupList.isEmpty() }
            if (senderLimit.isNotEmpty()) {
                senderLimit.filter { it.senderList.contains(sender.senderId) }.randomOrNull()?.let {
                    return it.returnMessage
                } ?: run {
                    limitMessage = senderLimit.random().permissionsMessage
                }
            }

            // 角色筛选
            val roleLimit = customizeCommandList.filter { it.senderList.isEmpty() && it.groupList.isEmpty() }
            if (roleLimit.isNotEmpty()) {
                val temp = roleLimit.filter { rolePermissions(it.role, sender.role) }
                temp.randomOrNull()?.let {
                    return it.returnMessage
                } ?: run { limitMessage = roleLimit.random().permissionsMessage }
            }

        }
        return limitMessage
    }
}
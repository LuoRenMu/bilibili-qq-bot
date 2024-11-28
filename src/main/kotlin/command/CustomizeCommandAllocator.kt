package cn.luorenmu.command

import cn.luorenmu.action.request.CustomizeRequestProcess
import cn.luorenmu.action.request.CustomizeRequestProcess.Companion.returnJsonFiled
import cn.luorenmu.command.entity.BotRole
import cn.luorenmu.command.entity.CommandSender
import cn.luorenmu.common.extensions.getStringZ
import cn.luorenmu.common.extensions.scanDollarString
import cn.luorenmu.common.utils.MatcherData
import cn.luorenmu.common.utils.file.CUSTOMIZE_COMMAND
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2024.11.27 12:17
 */
@Component
class CustomizeCommandAllocator(
    val customizeRequestProcess: CustomizeRequestProcess,
) {
    private val log = KotlinLogging.logger {}

    private fun rolePermissions(configRoleStr: String, senderRole: BotRole): Boolean {
        return BotRole.convert(configRoleStr).roleNumber <= senderRole.roleNumber
    }

    private fun commandMatcher(commandMessage: String, sender: CommandSender): Boolean {
        return sender.message.contains(commandMessage.toRegex())
    }

    fun process(sender: CommandSender): String? {
        val message = allocator(sender)
        message?.let {
            val fields = it.scanDollarString()
            if (fields.isEmpty()) {
                return it
            }

            var returnMessage = it
            fields.forEach { field ->
                val split = field.split('.')
                when (split[0]) {
                    "customize_request" -> {
                        customizeRequestProcess.process(sender.messageId, split[1])
                        val jsonField = field.replace("customize_request.", "")
                        returnJsonFiled[sender.messageId]?.let { json ->
                            returnMessage =
                                MatcherData.replaceDollardName(returnMessage, field, json.getStringZ(jsonField))
                        }
                    }

                    else -> {
                        log.error { "not support ${split[0]}" }
                    }
                }
            }
            return returnMessage
        }
        return null
    }


    private fun allocator(sender: CommandSender): String? {
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
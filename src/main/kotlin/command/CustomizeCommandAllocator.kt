package cn.luorenmu.command

import cn.luorenmu.action.request.CustomizeRequestProcess
import cn.luorenmu.command.entity.BotRole
import cn.luorenmu.command.entity.CommandSender
import cn.luorenmu.command.entity.DeepMessage
import cn.luorenmu.common.extensions.getStringZ
import cn.luorenmu.common.extensions.scanDollarString
import cn.luorenmu.common.utils.MatcherData
import cn.luorenmu.common.utils.file.CUSTOMIZE_COMMAND
import com.alibaba.fastjson2.parseObject
import com.alibaba.fastjson2.toJSONString
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

    fun process(sender: CommandSender): MutableList<String>? {
        val messageListOrNull = allocator(sender)
        messageListOrNull?.let { messageList ->
            for ((index, it) in messageList.withIndex()) {
                val fields = it.scanDollarString()
                if (fields.isEmpty()) {
                    continue
                }

                var returnMessage = it
                fields.forEach { field ->
                    val split = field.split('.')
                    when (split[0]) {
                        "customize_request" -> {
                            val returnJsonFiled = customizeRequestProcess.process(split[1])
                            val jsonField = field.replace("customize_request.", "")
                            returnJsonFiled?.let { json ->
                                returnMessage =
                                    MatcherData.replaceDollardName(returnMessage, field, json.getStringZ(jsonField))
                            } ?: run {
                                returnMessage =
                                    MatcherData.replaceDollardName(returnMessage, field, "请求失败")
                            }
                        }

                        "sender" -> {
                            val newField = field.replace("sender.", "")
                            val stringZ = sender.toJSONString().parseObject().getStringZ(newField)
                            stringZ?.let { senderInfo ->
                                returnMessage =
                                    MatcherData.replaceDollardName(returnMessage, field, senderInfo)
                            }
                        }

                        else -> {
                            log.error { "not support ${split[0]}" }
                        }
                    }
                }
                messageList[index] = returnMessage
            }
            return messageList
        }
        return null
    }


    private fun allocator(sender: CommandSender): MutableList<String>? {
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
                            return deepMessageConvert(it.returnMessage, it.deepMessage)
                        } ?: run {
                        groupLimitCustomizeCommandList.filter { rolePermissions(it.role, sender.role) }.randomOrNull()
                            ?.let {
                                return deepMessageConvert(it.returnMessage, it.deepMessage)
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
                    return deepMessageConvert(it.returnMessage, it.deepMessage)
                } ?: run {
                    limitMessage = senderLimit.random().permissionsMessage
                }
            }

            // 角色筛选
            val roleLimit = customizeCommandList.filter { it.senderList.isEmpty() && it.groupList.isEmpty() }
            if (roleLimit.isNotEmpty()) {
                val temp = roleLimit.filter { rolePermissions(it.role, sender.role) }
                temp.randomOrNull()?.let {
                    return deepMessageConvert(it.returnMessage, it.deepMessage)
                } ?: run { limitMessage = roleLimit.random().permissionsMessage }
            }

        }
        return limitMessage?.let {
            return mutableListOf(it)
        }
    }

    private fun deepMessageConvert(firstMessage: String?, deepMessage: DeepMessage?): MutableList<String>? {
        firstMessage?.let {
            val messageList = mutableListOf(it)
            var deepMessageTemp = deepMessage
            while (true) {
                deepMessageTemp?.let { dm ->
                    messageList.add(dm.message)
                    deepMessageTemp = dm.deepMessage
                } ?: break
            }
            return messageList
        }
        return null
    }
}
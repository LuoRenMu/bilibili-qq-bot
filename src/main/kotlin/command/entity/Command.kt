package cn.luorenmu.command.entity

import com.alibaba.fastjson2.annotation.JSONField

/**
 * @author LoMu
 * Date 2024.11.16 14:09
 */
data class Command(
    @JSONField(name    = "command_list")
    val commandList: MutableList<CommandInfo> = mutableListOf(),
)

data class CommandInfo(
    @JSONField(name = "command_id")
    var commandId: String = "",
    var command: String = "",
    @JSONField(name = "return_message")
    var returnMessage: String = "",
    var role: String = "group_admin",
)

enum class CommandId(val id: String) {
    REFRESH_CONFIG("refresh_config"),
    GET_LAST_ARTICLE("get_last_article"),
}





package cn.luorenmu.command.entity

import com.alibaba.fastjson2.annotation.JSONField
import kotlin.random.Random

/**
 * @author LoMu
 * Date 2024.11.27 12:01
 */
data class CustomizeCommand(
    @JSONField(name = "customize_command_list")
    val customizeCommandList: MutableList<CustomizeCommandInfo> = mutableListOf(),
)

data class CustomizeCommandInfo(
    @JSONField(name = "permissions_message")
    var permissionsMessageTemp: String = "",
    var command: String = "",
    @JSONField(name = "sender_list")
    var senderList: MutableList<Long> = mutableListOf(),
    @JSONField(name = "group_list")
    var groupList: MutableList<Long> = mutableListOf(),
    @JSONField(name = "return_message")
    var returnMessageTemp: String = "",
    @JSONField(name = "deep_message")
    var deepMessage: DeepMessage? = null,
    var role: String = "member",
    val probability: Double = 1.0,
) {
    private fun randomExecute(probability: Double): Boolean {
        return Random.nextDouble(0.0, 1.0) < probability
    }


    @JSONField(serialize = false)
    var permissionsMessage: String? = null
        get() {
            if (permissionsMessageTemp.isBlank()) {
                return null
            }
            return permissionsMessageTemp
        }


    @JSONField(serialize = false)
    var returnMessage: String? = null
        get() {
            if (returnMessageTemp.isBlank()) {
                return null
            }
            if (randomExecute(probability)) {
                return returnMessageTemp
            }
            return null
        }
}

data class DeepMessage(
    var message: String = "",
    @JSONField(name = "deep_message")
    var deepMessage: DeepMessage? = null,
)

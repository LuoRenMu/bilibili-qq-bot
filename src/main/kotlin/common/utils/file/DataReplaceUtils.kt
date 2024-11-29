package cn.luorenmu.common.utils.file

import cn.luorenmu.command.entity.CommandSender
import cn.luorenmu.common.extensions.getValueByPath
import com.alibaba.fastjson2.parseObject
import com.alibaba.fastjson2.toJSONString

/**
 * @author LoMu
 * Date 2024.11.29 21:58
 */
fun senderDataReplace(field: String, commandSender: CommandSender): String? {
    val senderJSONObject = commandSender.toJSONString().parseObject()
    val newField = field.replace("sender.", "")
    return senderJSONObject.getValueByPath(newField)
}
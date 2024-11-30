package cn.luorenmu.common.utils.file

import cn.hutool.core.io.resource.ResourceUtil
import cn.luorenmu.config.entity.MessageConvert
import cn.luorenmu.file.ReadWriteFile
import com.alibaba.fastjson2.to
import java.io.File

/**
 * @author LoMu
 * Date 2024.11.11 13:38
 */
object MessageConvertFileUtils {
    var MESSAGE_CONVERT: MessageConvert = MessageConvert()
    val MESSAGE_CONVERT_PATH = ReadWriteFile.CURRENT_PATH + "message_convert.json"

    @Synchronized
    fun initMessageConvert(messageConvert: MessageConvert? = null): Boolean {
        if (File(MESSAGE_CONVERT_PATH).exists()) {
            MESSAGE_CONVERT = loadMessageConvert()
            ReadWriteFile.entityWriteFile(MESSAGE_CONVERT_PATH, MESSAGE_CONVERT)
            return false
        }
        val json = messageConvert?.let {
            MESSAGE_CONVERT = it
            it
        } ?: run {
            val jsonStr =
                ResourceUtil.getResource("config/message_convert.json").openStream().bufferedReader().readText()
            jsonStr.to<MessageConvert>()
        }
        ReadWriteFile.entityWriteFile(MESSAGE_CONVERT_PATH, json)

        return true
    }

    @Synchronized
    fun loadMessageConvert(): MessageConvert {
        val json = ReadWriteFile.readFileJson(MESSAGE_CONVERT_PATH)
        val messageConvert = json.to<MessageConvert>()
        MESSAGE_CONVERT = messageConvert
        return messageConvert
    }

    @Synchronized
    fun updateMessageConvert(messageConvert: (MessageConvert) -> MessageConvert) {
        loadMessageConvert()
        val new = messageConvert(MESSAGE_CONVERT)

        File(MESSAGE_CONVERT_PATH).delete()
        initMessageConvert(new)
    }
}

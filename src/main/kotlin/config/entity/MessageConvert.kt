package cn.luorenmu.config.entity

import cn.luorenmu.common.utils.MatcherData
import com.alibaba.fastjson2.annotation.JSONField

/**
 * @author LoMu
 * Date 2024.11.11 13:32
 */
data class MessageConvert(
    @JSONField(name = "convert_messages")
    var convertMessages: List<MessageConvertFormat> = listOf(),
) {
    private var formatText: String = ""
    fun replace(id: String, format: String, text: String): MessageConvert {

        val covert = convertMessages.first { it.id == id }
        this.formatText = MatcherData.replaceDollardName(covert.formatMessage, format, text)

        return this
    }

    fun replace(format: String, text: String): MessageConvert {
        this.formatText = MatcherData.replaceDollardName(formatText, format, text)
        return this
    }

    fun build(): String {
        return this.formatText
    }

    class ID {
        companion object {
            const val BILIBILI_ARTICLE = "bilibili_article"
            const val FORWARD_TEXT = "forward_text"
            const val FORWARD_IMAGE = "forward_image"
        }
    }

    class FORMAT {
        companion object {
            const val ORIGIN_TEXT = "origin_text"
            const val ORIGIN_IMAGE = "origin_image"
            const val UP_NAME = "up_name"
        }
    }


}

data class MessageConvertFormat(
    val id: String,
    @JSONField(name = "format_messages")
    val formatMessage: String,
)

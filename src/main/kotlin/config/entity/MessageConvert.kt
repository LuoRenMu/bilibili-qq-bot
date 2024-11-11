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
    fun replace(id: String, format: String, text: String): String {
        convertMessages.firstOrNull { it.id == id }?.let {
            return MatcherData.replaceDollardName(it.formatMessage, format, text)
        } ?: run { return text }
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
        }
    }


}

data class MessageConvertFormat(
    val id: String,
    @JSONField(name = "format_messages")
    val formatMessage: String,
)

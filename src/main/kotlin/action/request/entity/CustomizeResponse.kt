package cn.luorenmu.action.request.entity

import com.alibaba.fastjson2.JSONObject

/**
 * @author LoMu
 * Date 2024.11.30 01:33
 */
data class CustomizeResponse(
    var success: Boolean,
    // 机器人发送消息
    var msg: String? = null,
    var jsonObject: JSONObject? = null,
) {
    constructor(jsonObject: JSONObject?) : this(jsonObject != null, null, jsonObject)
    constructor(success: Boolean, jsonObject: JSONObject?) : this(success, null, jsonObject)
}

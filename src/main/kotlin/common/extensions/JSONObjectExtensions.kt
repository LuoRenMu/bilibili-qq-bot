package cn.luorenmu.common.extensions

import com.alibaba.fastjson2.JSONException
import com.alibaba.fastjson2.JSONObject

/**
 * @author LoMu
 * Date 2024.11.28 18:20
 */
fun JSONObject.getStringZ(name: String): String? {
    val strings: Array<String> = name.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    var jsonObject = this
    var value: String? = null

    for (i in strings.indices) {
        try {
            val temp = jsonObject.getJSONObject(strings[i])
            if (temp != null) {
                jsonObject = temp
            } else {
                value = jsonObject.getString(
                    strings[i]
                )
            }
        } catch (var6: JSONException) {
            value = jsonObject.getString(strings[i])
        }
    }

    return value
}
package cn.luorenmu.common.extensions

import com.alibaba.fastjson2.JSONException
import com.alibaba.fastjson2.JSONObject
import io.github.oshai.kotlinlogging.KotlinLogging

/**
 * @author LoMu
 * Date 2024.11.28 18:20
 */

private val log = KotlinLogging.logger {}

fun JSONObject.getValueByPathSkipArray(name: String): String {
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

    return value ?: run { return jsonObject.toString() }
}

fun JSONObject.getValueByPath(path: String): String? {
    return this.getValueByPath(path.split("."))
}

fun JSONObject.getValueByPath(pathSegments: List<String>): String? {
    var current = this
    for (segment in pathSegments) {
        val parts = segment.split("[", "]", limit = 3).filter { it.isNotEmpty() }
        when (parts.size) {
            1 -> {
                try {
                    current = current.getJSONObject(parts[0]) ?: run { return current.getString(parts[0]) }
                } catch (e: JSONException) {
                    return current.getString(parts[0])
                }

            }

            2 -> {
                val index = parts[1].toIntOrNull()
                if (index != null && current.containsKey(parts[0])) {
                    val array = current.getJSONArray(parts[0])
                    if (array.isEmpty() || index >= array.size) {
                        log.error { "Index $index out of bounds for length ${array.size}" }
                        return null
                    }
                    current = array.getJSONObject(index) ?: return array[index].toString()
                } else {
                    return null
                }
            }

            else -> return null
        }
    }
    return current.toString()
}
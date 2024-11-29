package cn.luorenmu.common.extensions

import com.alibaba.fastjson2.JSONException
import com.alibaba.fastjson2.JSONObject

/**
 * @author LoMu
 * Date 2024.11.28 18:20
 */


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
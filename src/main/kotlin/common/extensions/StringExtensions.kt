package cn.luorenmu.common.extensions

/**
 * @author LoMu
 * Date 2024.11.28 18:04
 */

fun String.scanDollarString(): MutableList<String> {
    val regex = "\\$\\{([^}]*)}".toRegex()

    val result = mutableListOf<String>()
    val matches = regex.findAll(this)


    for (match in matches) {
        result.add(match.groupValues[1])
    }
    return result
}

fun String.replaceDollarString(field: String, replace: String): String {
    return this.replace("\${$field}", replace)
}
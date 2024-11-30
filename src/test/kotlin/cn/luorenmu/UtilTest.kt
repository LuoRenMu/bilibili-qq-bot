package cn.luorenmu

import cn.luorenmu.common.extensions.replaceDollarString
import cn.luorenmu.common.utils.MatcherData

/**
 * @author LoMu
 * Date 2024.11.16 14:21
 */


fun main() {
    println(
        "\${customize_request.get_img.data[0]}".replaceDollarString("customize_request.get_img.data[0]","123")
    )

}
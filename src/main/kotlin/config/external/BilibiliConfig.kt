package cn.luorenmu.config.external

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2024.09.14 12:51
 */
@Component
@ConfigurationProperties("bilibili")
data class BilibiliConfig(
    var limit : Int = 10,
    var delete : Int = 5,
)
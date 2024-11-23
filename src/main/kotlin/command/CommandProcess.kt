package cn.luorenmu.command

import cn.luorenmu.common.utils.loadCommandFile
import cn.luorenmu.common.utils.loadMessageConvert
import cn.luorenmu.common.utils.loadSetting
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2024.11.17 20:10
 */
val log = KotlinLogging.logger {}

@Component
class CommandProcess {
    fun refreshConfig(): String {
        try {
            loadSetting()
            loadCommandFile()
            loadMessageConvert()
        } catch (e: Exception) {
            log.error { e.stackTraceToString() }
            return "update config failed exception : ${e.message}"
        }
        log.info { "update config success" }
        return "update config success"
    }
}
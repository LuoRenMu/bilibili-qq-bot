package cn.luorenmu.command

import cn.luorenmu.common.utils.file.loadCommandFile
import cn.luorenmu.common.utils.file.loadCustomizeCommandFile
import cn.luorenmu.common.utils.file.loadMessageConvert
import cn.luorenmu.common.utils.file.loadSetting
import cn.luorenmu.task.BilibiliMessagePush
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2024.11.17 20:10
 */
val log = KotlinLogging.logger {}

@Component
class CommandProcess(
    private val bilibiliMessagePush: BilibiliMessagePush,
) {
    fun refreshConfig(): String {
        try {
            loadSetting()
            loadCommandFile()
            loadCustomizeCommandFile()
            loadMessageConvert()
        } catch (e: Exception) {
            log.error { e.stackTraceToString() }
            return "update config failed exception : ${e.message}"
        }
        log.info { "update config success" }
        return "update config success"
    }

    fun executeBilibiliTimingTask(): String {
        bilibiliMessagePush.timingPush()
        return "execute success"
    }
}
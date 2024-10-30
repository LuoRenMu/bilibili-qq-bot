package cn.luorenmu.task

import cn.luorenmu.action.listenProcess.BilibiliEventListen
import cn.luorenmu.config.external.BilibiliConfig
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.File
import java.time.LocalDateTime

/**
 * @author LoMu
 * Date 2024.09.03 14:45
 */
@Component
class DatabaseTask(
    private val config: BilibiliConfig,
) {

    @Scheduled(cron = "0 */5 * * * *")
    fun timingDeleteFile() {
        if (config.delete == 0) {
            return
        }
        if (BilibiliEventListen.bilibiliExistVideo.isNotEmpty()) {
            for (key in BilibiliEventListen.bilibiliExistVideo.keys()) {
                val videoDate = BilibiliEventListen.bilibiliExistVideo[key]
                if (videoDate!!.isAfter(LocalDateTime.now().plusHours(-config.delete.toLong()))) {
                    val file = File(key)
                    if (!file.exists() || file.delete()) {
                        BilibiliEventListen.bilibiliExistVideo.remove(key)
                    }
                }
            }
        }
    }
}
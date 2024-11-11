package cn.luorenmu.config

import cn.hutool.core.io.resource.ResourceUtil
import cn.luorenmu.MainApplication
import cn.luorenmu.action.listenProcess.BilibiliMessageCollect
import cn.luorenmu.common.utils.*
import cn.luorenmu.config.entity.groups
import cn.luorenmu.file.InitializeFile
import cn.luorenmu.file.ReadWriteFile
import com.mikuac.shiro.core.BotContainer
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Configuration
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

/**
 * @author LoMu
 * Date 2024.07.25 2:16
 */

val log = KotlinLogging.logger { }

@Configuration
class MultiFunctionConfig(
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    private val botContainer: BotContainer,
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    private val bilibiliMessageCollect: BilibiliMessageCollect,
) : ApplicationListener<ApplicationReadyEvent> {


    private val dirs = listOf(
        "image/", "request/", "video/bilibili/"
    )


    private val resourcesRequestJson = mapOf(
        "bilibili_request" to "request/bilibili_request.json",
    )

    init {
        InitializeFile.run(MainApplication::class.java)
        for (dir in dirs) {
            ReadWriteFile.createCurrentDirs(dir)
        }

        /**
         * 文件已存在则跳过
         */
        for (file in resourcesRequestJson) {
            val stream = ResourceUtil.getResource(file.value).openStream()
            ReadWriteFile.writeCurrentStreamFile(file.value, stream)
            val json = ReadWriteFile.readCurrentFileJson(file.value)
            JsonObjectUtils.putRequestJson(file.key, json)
        }

        initMessageConvert()

    }


    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        log.info { "LoMu Bot running successfully!" }

        // 与qq成功建立websocket链接
        thread {
            while (true) {
                if (botContainer.robots.isNotEmpty()) {
                    break
                }
            }

            if (initSetting()) {
                updateSetting {
                    val bot = botContainer.robots.values.first()
                    val groupList = bot!!.groupList.data.map { n -> n.groupId }
                    groups = groupList
                    it.bannedGroupBilibiliPush = groupList
                    it.bannedGroupBvidListen = groupList
                    it
                }
            }

            // 如果视频不是持久化保存 则在启动时删除所有
            if (SETTING.bilibiliDelete != 0) {
                val file = File(getVideoPath("bilibili/"))
                val listFiles = file.listFiles()
                if (listFiles != null && listFiles.isNotEmpty()) {
                    listFiles.forEach {
                        it.delete()
                    }
                }
            }

            SETTING.listenList.forEach {
                if (it.uid.isNotBlank() && !BilibiliCacheUtils.exists(it.uid)) {
                    val articleMessageCollect = bilibiliMessageCollect.articleMessageCollect(it.uid, 4)
                    val id = articleMessageCollect.maxBy { bilibiliArticle -> bilibiliArticle.id.toLong() }.id.toLong()
                    val bilibiliCacheUtils = BilibiliCacheUtils(it.uid, id)
                    bilibiliCacheUtils.writeCache()
                    TimeUnit.SECONDS.sleep(3)
                }
            }
        }

    }

}
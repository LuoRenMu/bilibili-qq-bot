package cn.luorenmu.config

import cn.hutool.core.io.resource.ResourceUtil
import cn.luorenmu.MainApplication
import cn.luorenmu.action.listenProcess.BilibiliMessageCollect
import cn.luorenmu.common.utils.*
import cn.luorenmu.common.utils.file.*
import cn.luorenmu.file.InitializeFile
import cn.luorenmu.file.ReadWriteFile
import com.alibaba.fastjson2.JSONObject
import com.alibaba.fastjson2.to
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Configuration
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * @author LoMu
 * Date 2024.07.25 2:16
 */

val log = KotlinLogging.logger { }


@Configuration
class BotStrapConfig(
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    val bilibiliMessageCollect: BilibiliMessageCollect,
) : ApplicationListener<ApplicationReadyEvent> {

    private val log = KotlinLogging.logger { }

    private val dirs = listOf(
        "image/", "request/", "video/bilibili/"
    )


    private val resourcesRequestJson = mapOf(
        "bilibili_request" to "request/bilibili_request.json",
    )


    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        log.info { "LoMu Bot running successfully!" }

        InitializeFile.run(MainApplication::class.java)
        for (dir in dirs) {
            ReadWriteFile.createCurrentDirs(dir)
        }


        for (filePath in resourcesRequestJson) {
            var initJsonObj =
                ResourceUtil.getResource(filePath.value).openStream().bufferedReader().readText().to<JSONObject>()
            val file = File(ReadWriteFile.CURRENT_PATH + filePath.value)
            if (file.exists()) {
                var fix = false
                val jsonObj = ReadWriteFile.readCurrentFileJson(filePath.value).to<JSONObject>()
                for (mutableEntry in initJsonObj) {
                    if (!jsonObj.containsKey(mutableEntry.key)) {
                        log.warn { "${filePath.value}-> ${mutableEntry.key} not exists!  try fix" }
                        fix = true
                        jsonObj[mutableEntry.key] = mutableEntry.value
                        log.info { "try success" }
                    }
                }
                if (fix) {
                    file.delete()
                    ReadWriteFile.entityWriteFileToCurrentDir(filePath.value, jsonObj)
                }

            }else{
                ReadWriteFile.entityWriteFileToCurrentDir(filePath.value, initJsonObj)
                initJsonObj = ReadWriteFile.readCurrentFileJson(filePath.value).to<JSONObject>()
            }

            JsonObjectUtils.putRequestJson(filePath.key, initJsonObj)
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



        initSetting()
        initCommandFile()
        initCustomizeCommandFile()
        initMessageConvert()
        initCustomizeRequest()

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
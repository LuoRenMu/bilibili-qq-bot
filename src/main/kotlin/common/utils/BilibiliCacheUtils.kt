package cn.luorenmu.common.utils

import cn.luorenmu.file.ReadWriteFile
import com.alibaba.fastjson2.to
import java.io.File

/**
 * @author LoMu
 * Date 2024.11.07 22:01
 */
data class BilibiliCacheUtils(
    var uid: String,
    var lastArticle: Long? = null,
    var lastVideo: String? = null,
) {
    private val jsonPath = "${ReadWriteFile.CURRENT_PATH}/cache/$uid.json"

    companion object {
        fun exists(uid: String): Boolean {
            return File("${ReadWriteFile.CURRENT_PATH}/cache/$uid.json").exists()
        }
    }

    @Synchronized
    fun writeCache() {
        ReadWriteFile.entityWriteFile(jsonPath, this)
    }
    @Synchronized
    fun deleteThenWriteCache() {
        File(jsonPath).delete()
        ReadWriteFile.entityWriteFile(jsonPath, this)
    }

    @Synchronized
    fun readCache(): BilibiliCacheUtils {
        val readFileJson = ReadWriteFile.readFileJson(jsonPath)
        return readFileJson.to<BilibiliCacheUtils>()
    }
}
package cn.luorenmu.common.utils.file

import cn.luorenmu.file.ReadWriteFile

/**
 * @author LoMu
 * Date 2024.08.03 9:34
 */
object FilePathUtils {
    fun getImagePath(name: String): String = ReadWriteFile.currentPathFileName("image/${name}.png").substring(1)
    fun getVideoPath(name: String): String = ReadWriteFile.currentPathFileName("video/${name}").substring(1)
}
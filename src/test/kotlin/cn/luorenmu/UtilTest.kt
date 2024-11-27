package cn.luorenmu

import cn.luorenmu.common.utils.file.initCommandFile
import cn.luorenmu.file.InitializeFile

/**
 * @author LoMu
 * Date 2024.11.16 14:21
 */


fun main() {
    InitializeFile.run(MainApplication::class.java)
    initCommandFile()
}
package cn.luorenmu.common.utils

import cn.hutool.core.io.resource.ResourceUtil
import cn.luorenmu.command.entity.Command
import cn.luorenmu.file.ReadWriteFile
import com.alibaba.fastjson2.to
import java.io.File

/**
 * @author LoMu
 * Date 2024.11.16 14:10
 */
var COMMAND: Command = Command()
val COMMAND_PATH = ReadWriteFile.CURRENT_PATH + "command.json"
val CUSTOMIZER_COMMAND_PATH = ReadWriteFile.CURRENT_PATH + "customize_command.json"

@Synchronized
fun initCommandFile(command: Command? = null): Boolean {
    var returnBool = false

    command?.let {
        COMMAND = it
        ReadWriteFile.entityWriteFile(COMMAND_PATH, COMMAND)
    }

    // 首次创建文件
    if (!File(COMMAND_PATH).exists()) {
        val initCommand =
            ResourceUtil.getResource("config/command.json").openStream().bufferedReader().readText().to<Command>()

        ReadWriteFile.entityWriteFile(COMMAND_PATH, initCommand)
        returnBool = true
    }

    if (!File(CUSTOMIZER_COMMAND_PATH).exists()) {
        val customizeCommand =
            ResourceUtil.getResource("config/customize_command.json").openStream().bufferedReader().readText()
                .to<Command>()
        ReadWriteFile.entityWriteFile(CUSTOMIZER_COMMAND_PATH, customizeCommand)
    }

    loadCommandFile()
    return returnBool
}

@Synchronized
fun loadCommandFile(): Command {
    val command = ReadWriteFile.readFileJson(COMMAND_PATH).to<Command>()
    val customizeCommand = ReadWriteFile.readFileJson(CUSTOMIZER_COMMAND_PATH).to<Command>()
    command.commandList.addAll(customizeCommand.commandList)

    COMMAND = command
    return COMMAND
}

@Synchronized
fun updateCommandFile(command: (Command) -> Command) {
    loadCommandFile()
    val new = command(COMMAND)

    File(COMMAND_PATH).delete()
    initCommandFile(new)
}

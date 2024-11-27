package cn.luorenmu.common.utils.file

import cn.hutool.core.io.resource.ResourceUtil
import cn.luorenmu.command.entity.Command
import cn.luorenmu.file.ReadWriteFile
import com.alibaba.fastjson2.to
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.File

/**
 * @author LoMu
 * Date 2024.11.16 14:10
 */
private val log = KotlinLogging.logger {}
var COMMAND: Command = Command()
val COMMAND_PATH = ReadWriteFile.CURRENT_PATH + "command.json"


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


    loadCommandFile()
    fixCommandFile()

    return returnBool
}

@Synchronized
fun fixCommandFile() {
    val initCommand =
        ResourceUtil.getResource("config/command.json").openStream().bufferedReader().readText().to<Command>()
    val commandIdList = COMMAND.commandList.map { it.commandId }
    for (commandInfo in initCommand.commandList) {
        if (!commandIdList.contains(commandInfo.commandId)) {
            log.error { "commandID: ${commandInfo.commandId} not found \t try fix " }
            updateCommandFile { command ->
                command.commandList.add(commandInfo)
                command
            }
            log.info { "${commandInfo.commandId} fix success" }
        }
    }
}

@Synchronized
fun loadCommandFile(): Command {
    val command = ReadWriteFile.readFileJson(COMMAND_PATH).to<Command>()
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

package cn.luorenmu.common.utils.file

import cn.hutool.core.io.resource.ResourceUtil
import cn.luorenmu.command.entity.CustomizeCommand
import cn.luorenmu.file.ReadWriteFile
import com.alibaba.fastjson2.to
import java.io.File

/**
 * @author LoMu
 * Date 2024.11.27 12:07
 */

var CUSTOMIZE_COMMAND = CustomizeCommand()
val CUSTOMIZER_COMMAND_PATH = ReadWriteFile.CURRENT_PATH + "customize_command.json"


@Synchronized
fun initCustomizeCommandFile(customizeCommand: CustomizeCommand? = null): Boolean {
    customizeCommand?.let {
        CUSTOMIZE_COMMAND = it
        ReadWriteFile.entityWriteFile(COMMAND_PATH, COMMAND)
    }

    if (!File(CUSTOMIZER_COMMAND_PATH).exists()) {
        val initCustomizeCommand =
            ResourceUtil.getResource("config/customize_command.json").openStream().bufferedReader().readText()
                .to<CustomizeCommand>()

        CUSTOMIZE_COMMAND = initCustomizeCommand
        ReadWriteFile.entityWriteFile(CUSTOMIZER_COMMAND_PATH, initCustomizeCommand)
        return true
    } else {
        loadCustomizeCommandFile()
    }

    return false
}

@Synchronized
fun loadCustomizeCommandFile(): CustomizeCommand {
    val customizeCommand = ReadWriteFile.readFileJson(CUSTOMIZER_COMMAND_PATH).to<CustomizeCommand>()
    CUSTOMIZE_COMMAND = customizeCommand

    return CUSTOMIZE_COMMAND
}

package cn.luorenmu.common.utils

import cn.luorenmu.config.entity.LoMuBotSetting
import cn.luorenmu.file.ReadWriteFile
import com.alibaba.fastjson2.to
import java.io.File

/**
 * @author LoMu
 * Date 2024.11.03 17:49
 */
var SETTING: LoMuBotSetting = LoMuBotSetting()
val SETTING_PATH = ReadWriteFile.CURRENT_PATH + "setting.json"


/**
 *  没有setting文件 初次创建返回true
 */
@Synchronized
fun initSetting(lomuBotSetting: LoMuBotSetting = LoMuBotSetting()): Boolean {
    if (File(SETTING_PATH).exists()) {
        SETTING = loadSetting()
        // 更新配置文件 在原有的基础上添加了更多字段
        ReadWriteFile.entityWriteFile(SETTING_PATH, SETTING)
        return false
    }
    ReadWriteFile.entityWriteFile(SETTING_PATH, lomuBotSetting)
    SETTING = lomuBotSetting
    return true
}

@Synchronized
fun loadSetting(): LoMuBotSetting {
    val json = ReadWriteFile.readFileJson(SETTING_PATH)
    val setting = json.to<LoMuBotSetting>()
    SETTING = setting
    return setting
}

@Synchronized
fun updateSetting(setting: (LoMuBotSetting) -> LoMuBotSetting) {
    loadSetting()
    val new = setting(SETTING)

    File(SETTING_PATH).delete()
    initSetting(new)
}

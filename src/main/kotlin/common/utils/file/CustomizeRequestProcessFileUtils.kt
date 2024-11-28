package cn.luorenmu.common.utils.file

import cn.hutool.core.io.resource.ResourceUtil
import cn.luorenmu.action.request.entity.CustomizeRequest
import cn.luorenmu.file.ReadWriteFile
import com.alibaba.fastjson2.to
import java.io.File

/**
 * @author LoMu
 * Date 2024.11.28 11:37
 */
var CUSTOMIZE_REQUEST = CustomizeRequest()
var CUSTOMIZE_REQUEST_PATH = ReadWriteFile.CURRENT_PATH + "customize_request_process.json"

@Synchronized
fun initCustomizeRequest(): Boolean {
    if (!File(CUSTOMIZE_REQUEST_PATH).exists()) {
        val initCustomizeRequest =
            ResourceUtil.getResource("config/customize_request_process.json").openStream().bufferedReader().readText()
                .to<CustomizeRequest>()

        CUSTOMIZE_REQUEST = initCustomizeRequest
        ReadWriteFile.entityWriteFile(CUSTOMIZE_REQUEST_PATH, initCustomizeRequest)
        return true
    } else {
        loadCustomizeRequestFile()
    }
    return false
}

@Synchronized
fun loadCustomizeRequestFile(): CustomizeRequest {
    val customizeRequest = ReadWriteFile.readFileJson(CUSTOMIZE_REQUEST_PATH).to<CustomizeRequest>()
    CUSTOMIZE_REQUEST = customizeRequest

    return CUSTOMIZE_REQUEST
}

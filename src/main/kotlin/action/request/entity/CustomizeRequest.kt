package cn.luorenmu.action.request.entity

import cn.luorenmu.entiy.Request.RequestDetailed
import com.alibaba.fastjson2.annotation.JSONField

/**
 * @author LoMu
 * Date 2024.11.28 10:55
 */
data class CustomizeRequest(
    @JSONField(name = "request_list")
    var requestList: MutableList<RequestInfo> = mutableListOf(),
)

data class RequestInfo(
    var id: String,
    @JSONField(name = "request_detailed")
    var requestDetailed: RequestDetailed,
    @JSONField(name = "response_process")
    var responseProcess: ResponseProcess,
)


data class ResponseProcess(
    @JSONField(name = "condition_process")
    var conditionProcess: ConditionProcess? = null,
    @JSONField(name = "return_json_filed")
    var returnJsonFiled: MutableList<String>? = null,
    var download: DownloadProcess?,
)

data class DownloadProcess(
    @JSONField(name = "download_filed")
    var downloadFiled: String = "",
    @JSONField(name = "download_path")
    var downloadPath: String = "",
)

data class ConditionProcess(
    var condition: String = "",
    @JSONField(name = "not_exists")
    var notExists: NotExistsProcess = NotExistsProcess(),
)


data class NotExistsProcess(
    var process: NotExists = NotExists.IGNORE,
    @JSONField(name = "try_count")
    var tryCount: Int? = null,
    var interval: Int? = null,
)

enum class NotExists {
    IGNORE,
    RE_REQUEST,
}
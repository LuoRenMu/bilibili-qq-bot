package cn.luorenmu.action.request

import cn.luorenmu.action.request.entity.CustomizeResponse
import cn.luorenmu.action.request.entity.NotExists
import cn.luorenmu.action.request.entity.ResponseProcess
import cn.luorenmu.command.entity.CommandSender
import cn.luorenmu.common.extensions.getValueByPath
import cn.luorenmu.common.extensions.scanDollarString
import cn.luorenmu.common.utils.MatcherData
import cn.luorenmu.common.utils.file.CustomizeRequestProcessFileUtils.CUSTOMIZE_REQUEST
import cn.luorenmu.entiy.Request.RequestDetailed
import cn.luorenmu.request.RequestController
import com.alibaba.fastjson2.*
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author LoMu
 * Date 2024.11.28 10:55
 */
@Component
class CustomizeRequestProcess(
    val requestData: RequestData,
) {

    private val log = KotlinLogging.logger {}


    fun processRequest(requestDetailedTemp: RequestDetailed, message: String): CustomizeResponse {
        // 深拷贝
        val requestDetailed = requestDetailedTemp.toJSONString().to<RequestDetailed>()
        val urlRegex = requestDetailed.url.scanDollarString()
        requestDetailed.body?.let {
            if (it.isNotEmpty()) {
                for (requestParam in it) {
                    val bodyParamRegex = requestParam.content.scanDollarString()
                    bodyParamRegex.forEach { regex ->
                        regex.toRegex().find(message)?.let { matchResult ->
                            requestParam.content = MatcherData.replaceDollardName(
                                requestParam.content,
                                regex,
                                matchResult.groups[1]!!.value
                            )
                        } ?: run {
                            log.error { "Regex does not match $requestParam" }
                        }
                    }
                }
            }
        }

        val request = RequestController(requestDetailed)
        urlRegex.forEach {
            it.toRegex().find(message)?.let { matchResult ->
                request.replaceUrl(it, matchResult.groups[1]!!.value)
            } ?: run {
                log.error { "${requestDetailedTemp.url} not found $it  message: $message" }
            }

        }


        try {
            request.request()?.let { httpResponse ->
                try {
                    return CustomizeResponse(httpResponse.body().parseObject())
                } catch (e: JSONException) {
                    return CustomizeResponse(httpResponse.body().parseArray<JSONObject>().first())
                }
            } ?: run {
                log.error { "request failed with id ${requestDetailed.url}" }
            }
        } catch (e: Exception) {
            log.error { e.printStackTrace() }
        }
        return CustomizeResponse(false, "请求失败")
    }

    fun processCondition(condition: String, jsonObject: JSONObject): Boolean {
        val jsonStr = jsonObject.toString()
        return jsonStr.contains(condition.toRegex())
    }


    fun processReturnJsonFiled(
        customizeRequestId: String,
        thisUrl: String,
        responseProcess: ResponseProcess,
        jsonObject: JSONObject,
    ): JSONObject? {
        responseProcess.returnJsonFiled?.let { jsonFiled ->
            val json = JSONObject()
            json["this"] = thisUrl
            json["uuid"] = UUID.randomUUID().toString()
            jsonFiled.forEach {
                jsonObject.getValueByPath(it)?.let { str ->
                    json[it.replace(".", "-")] = str
                } ?: run {
                    log.error { "$customizeRequestId json filed $it not found" }
                    return null
                }
            }

            return JSONObject().apply {
                put(customizeRequestId, json)
            }
        }
        return null
    }


    fun process(id: String, commandSender: CommandSender): CustomizeResponse {
        try {
            CUSTOMIZE_REQUEST.requestList.firstOrNull { it.id == id }?.let { customizeRequest ->
                return processRequest(
                    customizeRequest.requestDetailed,
                    commandSender.message
                ).let { customizeResponse ->
                    var response: JSONObject? = customizeResponse.jsonObject
                    val responseProcess = customizeRequest.responseProcess
                    responseProcess.conditionProcess?.let { conditionProcess ->
                        when (conditionProcess.notExists.process) {
                            NotExists.RE_REQUEST -> {
                                val tryCount = conditionProcess.notExists.tryCount ?: 5
                                val interval = conditionProcess.notExists.interval ?: 1
                                var requestSuccess = false
                                for (i in 0 until tryCount) {
                                    if (response != null && processCondition(conditionProcess.condition, response!!)) {
                                        requestSuccess = true
                                        break
                                    } else {
                                        response = processRequest(
                                            customizeRequest.requestDetailed,
                                            commandSender.message
                                        ).jsonObject
                                    }
                                    TimeUnit.SECONDS.sleep(interval.toLong())
                                }
                                if (!requestSuccess) {
                                    log.warn { "customize id : ${customizeRequest.id} -> Unable to meet condition Retry exceeded the upper limit  " }
                                    return CustomizeResponse(false, "请求超出限制,仍无法获得有效消息")
                                }
                            }

                            NotExists.IGNORE -> {
                                if (!customizeResponse.success || !processCondition(
                                        conditionProcess.condition,
                                        response!!
                                    )
                                ) {
                                    log.warn { "customize id : ${customizeRequest.id} -> Failure to meet conditions was ignored " }
                                    return CustomizeResponse(false, "请求失败")
                                }
                            }
                        }
                    }


                    processDownload(responseProcess, response!!)

                    val returnJsonFiled = processReturnJsonFiled(
                        customizeRequest.id, customizeRequest.requestDetailed.url, responseProcess, response!!
                    )
                    CustomizeResponse(returnJsonFiled)
                }
            } ?: run {
                log.error { "customize request not found or failed id : $id" }
            }
        } catch (e: Exception) {
            log.error { e.printStackTrace() }
        }
        return CustomizeResponse(false, "内部错误")
    }


    @Async
    fun processDownload(responseProcess: ResponseProcess, response: JSONObject) {
        responseProcess.download?.let { download ->
            var path = download.downloadPath
            val fields = path.scanDollarString()
            fields.forEach { field ->
                val valueByPath = response.getValueByPath(field)
                path = MatcherData.replaceDollardName(path, field, valueByPath)
            }
            requestData.downloadStream(
                response.getValueByPath(download.downloadFiled)!!, path
            )
        }
    }
}


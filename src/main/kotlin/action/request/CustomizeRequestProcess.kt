package cn.luorenmu.action.request

import cn.luorenmu.action.request.entity.NotExists
import cn.luorenmu.common.extensions.getStringZ
import cn.luorenmu.common.extensions.scanDollarString
import cn.luorenmu.common.utils.MatcherData
import cn.luorenmu.common.utils.file.CUSTOMIZE_REQUEST
import cn.luorenmu.request.RequestController
import com.alibaba.fastjson2.JSONException
import com.alibaba.fastjson2.JSONObject
import com.alibaba.fastjson2.parseArray
import com.alibaba.fastjson2.parseObject
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

/**
 * @author LoMu
 * Date 2024.11.28 10:55
 */
@Component
class CustomizeRequestProcess(
    val requestData: RequestData,
) {

    private val log = KotlinLogging.logger {}

    companion object {
        val returnJsonFiled = mutableMapOf<Int, JSONObject>()
        val returnMessage = mutableMapOf<Int, String>()
    }

    fun processRequest(id: String): JSONObject? {
        val requestInfo = CUSTOMIZE_REQUEST.requestList.firstOrNull { it.id == id }
        requestInfo?.let {
            val request = RequestController(it.requestDetailed)
            request.request()?.let { httpResponse ->
                try {
                    return httpResponse.body().parseObject()
                } catch (e: JSONException) {
                    return httpResponse.body().parseArray<JSONObject>().first()
                }

            } ?: run {
                log.error { "request failed with id $id" }
            }
        } ?: run {
            log.error { "customize request  $id not found" }
        }
        return null
    }

    fun processCondition(condition: String, jsonObject: JSONObject): Boolean {
        val jsonStr = jsonObject.toString()
        return jsonStr.contains(condition.toRegex())
    }

    fun processReturnMessage(id: Int, regexString: String, jsonObject: JSONObject): String {
        val regex = regexString.toRegex()
        val jsonString = jsonObject.toJSONString()
        val str = StringBuilder()
        regex.findAll(jsonString).toList().forEach {
            str.append(it.groupValues[1])
        }
        returnMessage[id] = str.toString()
        return str.toString()
    }

    fun processReturnJsonFiled(
        id: Int,
        customizeRequestId: String,
        thisUrl: String,
        jsonFiled: MutableList<String>,
        jsonObject: JSONObject,
    ): JSONObject {
        val json = JSONObject()
        json["this"] = thisUrl
        json["uuid"] = UUID.randomUUID().toString()
        jsonFiled.forEach {
            if (jsonObject.containsKey(it)) {
                json[it] = jsonObject[it].toString()
            } else {
                log.error { "$id json filed $it not found" }
            }
        }

        val returnJson = JSONObject().apply {
            put(customizeRequestId, json)
        }
        returnJsonFiled[id] = returnJson
        return json
    }


    fun process(messageId: Int, id: String) {
        CUSTOMIZE_REQUEST.requestList.firstOrNull { it.id == id }?.let { customizeRequest ->
            processRequest(customizeRequest.id)?.let { responseTemp ->
                var response: JSONObject? = responseTemp
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
                                    response = processRequest(customizeRequest.id)
                                }
                                TimeUnit.SECONDS.sleep(interval.toLong())
                            }
                            if (!requestSuccess) {
                                log.warn { "customize id : ${customizeRequest.id} -> Unable to meet condition Retry exceeded the upper limit  " }
                                return
                            }
                        }

                        NotExists.IGNORE -> {
                            if (!processCondition(conditionProcess.condition, response!!)) {
                                log.warn { "customize id : ${customizeRequest.id} -> Failure to meet conditions was ignored " }
                                return
                            }
                        }
                    }
                }
                responseProcess.returnMessage?.let { message ->
                    processReturnMessage(messageId, message, response!!)
                }
                responseProcess.returnJsonFiled?.let { jsonFiled ->
                    processReturnJsonFiled(
                        messageId,
                        customizeRequest.id,
                        customizeRequest.requestDetailed.url,
                        jsonFiled,
                        response!!
                    )
                }
                responseProcess.download?.let { download ->
                    var path = download.downloadPath
                    val fields = path.scanDollarString()
                    fields.forEach { field ->
                        path = MatcherData.replaceDollardName(path, field, response!!.getStringZ(field))
                    }
                    thread(start = true) {
                        requestData.downloadStream(
                            response!!.getStringZ(download.downloadFiled)!!,
                            path
                        )
                    }
                }
            }
        } ?: run {
            log.error { "customize request not found or failed id : $id" }
        }
    }
}


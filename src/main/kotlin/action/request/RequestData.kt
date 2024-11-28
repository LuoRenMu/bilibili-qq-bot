package cn.luorenmu.action.request

import cn.luorenmu.entiy.Request
import cn.luorenmu.entiy.Request.RequestParam
import cn.luorenmu.file.ReadWriteFile
import cn.luorenmu.request.RequestController

/**
 * @author LoMu
 * Date 2024.11.28 11:35
 */
open class RequestData {
    fun downloadStream(url: String, outputPath: String, headers: MutableList<RequestParam>? = null): Boolean {
        val requestDetailed = Request.RequestDetailed()
        requestDetailed.url = url
        requestDetailed.method = "GET"
        requestDetailed.headers = headers
        val requestController = RequestController(requestDetailed)
        val resp = requestController.request()
        resp?.let {
            val stream = resp.bodyStream()
            ReadWriteFile.writeStreamFile(outputPath, stream)
            return true
        }
        return false
    }

}
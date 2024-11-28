package cn.luorenmu

import cn.luorenmu.entiy.Request
import cn.luorenmu.request.RequestController
import com.alibaba.fastjson2.JSONObject
import com.alibaba.fastjson2.parseArray
import com.alibaba.fastjson2.parseObject
import com.alibaba.fastjson2.toJSONString

/**
 * @author LoMu
 * Date 2024.11.16 14:21
 */


fun main() {
    val request = RequestController(
        Request.RequestDetailed().apply {
            url = "https://image.anosu.top/pixiv/json?r18=0&keyword=arknights"
            method = "GET"
        }).request().body().parseObject()
    println(request)
}
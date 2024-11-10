package cn.luorenmu

import cn.luorenmu.action.listenProcess.BilibiliMessageCollect
import cn.luorenmu.action.listenProcess.BilibiliRequestData

/**
 * @author LoMu
 * Date 2024.11.02 18:50
 */


fun main() {
    val bilibiliMessageCollect = BilibiliMessageCollect(BilibiliRequestData())
    val articleMessageCollect = bilibiliMessageCollect.articleMessageCollect("161775300", 10)
    articleMessageCollect.forEach {
        println("=========")
        println(it)
        println("=========")
    }

}
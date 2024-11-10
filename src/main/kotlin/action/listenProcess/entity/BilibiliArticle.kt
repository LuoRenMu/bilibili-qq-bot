package cn.luorenmu.action.listenProcess.entity

/**
 * @author LoMu
 * Date 2024.11.06 18:46
 */
data class BilibiliArticle(
    val id: String,
    val text: String,
    val picUrl: List<String>,
    val isTopping: Boolean,
)
package cn.luorenmu.action.listenProcess

import cn.luorenmu.action.listenProcess.entity.BilibiliArticle
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2024.11.06 18:25
 */
@Component
class BilibiliMessageCollect(
    private val bilibiliRequestData: BilibiliRequestData,
) {
    fun articleMessageCollect(uid: String, num: Int): ArrayList<BilibiliArticle> {
        val space = bilibiliRequestData.space(uid)
        val articleList = arrayListOf<BilibiliArticle>()
        space?.let { bilibiliSpace ->
            if (bilibiliSpace.code == 0L) {
                val items = bilibiliSpace.data.items
                items.subList(0, num).forEach { bilibiliItem ->
                    val isTopping = bilibiliItem.modules.moduleTag?.text == "置顶"
                    val text = bilibiliItem.modules.moduleDynamic.desc!!.text
                    val idStr = bilibiliItem.idStr
                    val imageUrls = bilibiliItem.modules.moduleDynamic.major?.draw?.items?.map { it.src } ?: listOf()
                    articleList.add(BilibiliArticle(idStr, text, imageUrls, isTopping))
                }
            }
        }
        return articleList
    }

}
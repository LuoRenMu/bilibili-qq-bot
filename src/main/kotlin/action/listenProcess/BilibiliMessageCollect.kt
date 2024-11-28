package cn.luorenmu.action.listenProcess

import cn.luorenmu.action.listenProcess.entity.BilibiliArticle
import cn.luorenmu.action.request.BilibiliRequestData
import com.mikuac.shiro.common.utils.MsgUtils
import org.springframework.stereotype.Component

/**
 * @author LoMuz
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
                    var bvid: String? = null
                    val imageUrls =
                        bilibiliItem.modules.moduleDynamic.major?.draw?.items?.map { it.src }?.toMutableList()
                            ?: arrayListOf()
                    val name = bilibiliItem.modules.moduleAuthor.name
                    val text = bilibiliItem.modules.moduleDynamic.desc?.text ?: run {
                        val text = MsgUtils.builder()

                        bilibiliItem.modules.moduleDynamic.major?.archive?.let { archive ->
                            text.text(
                                MsgUtils.builder().text(archive.title).img(archive.cover).text("\n${archive.jumpUrl}")
                                    .build()
                            )
                            bvid = archive.bvid
                        } ?: run {
                            "暂时无法获取的数据类型"
                        }
                        text.build()
                    }
                    val idStr = bilibiliItem.idStr
                    articleList.add(BilibiliArticle(idStr, name, text, imageUrls, isTopping, bvid))
                }
            }
        }
        return articleList
    }

}
package cn.luorenmu.listen

import cn.luorenmu.action.listenProcess.BilibiliMessageCollect
import cn.luorenmu.action.listenProcess.BilibiliRequestData
import cn.luorenmu.common.extensions.sendPrivateBilibiliArticle
import cn.luorenmu.common.utils.SETTING
import cn.luorenmu.common.utils.loadSetting
import com.mikuac.shiro.annotation.PrivateMessageHandler
import com.mikuac.shiro.annotation.PrivateMsgDeleteNoticeHandler
import com.mikuac.shiro.annotation.common.Shiro
import com.mikuac.shiro.core.Bot
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent
import com.mikuac.shiro.dto.event.notice.PrivateMsgDeleteNoticeEvent
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2024.07.05 7:58
 */

@Component
@Shiro
class PrivateEvenListen(
    val bilibiliMessageCollect: BilibiliMessageCollect,
    val bilibiliRequestData: BilibiliRequestData,
) {

    @PrivateMessageHandler
    fun privateMessageHandler(bot: Bot, privateMessage: PrivateMessageEvent) {
        if (privateMessage.privateSender.userId == SETTING.botOwner) {
            when (privateMessage.message) {
                "更新配置文件" -> {
                    loadSetting()
                    bot.sendPrivateMsg(privateMessage.userId, "success", false)
                    return
                }
            }
            if (privateMessage.message.startsWith("最新动态")) {
                val uid = privateMessage.message.split(" ")[1]
                val articleMessageCollect = bilibiliMessageCollect.articleMessageCollect(uid, 5)
                bot.sendPrivateBilibiliArticle(
                    articleMessageCollect.maxBy { it.id.toLong() },
                    bilibiliRequestData,
                    privateMessage.userId
                )
            }
        }
    }

    @PrivateMsgDeleteNoticeHandler
    fun privateDeleteHandler(bot: Bot, privateMessage: PrivateMsgDeleteNoticeEvent) {

    }
}
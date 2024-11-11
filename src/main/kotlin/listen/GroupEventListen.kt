package cn.luorenmu.listen

import cn.luorenmu.action.listenProcess.BilibiliEventListen
import cn.luorenmu.action.listenProcess.BilibiliMessageCollect
import cn.luorenmu.action.listenProcess.BilibiliRequestData
import cn.luorenmu.common.extensions.sendGroupBilibiliarticle
import cn.luorenmu.common.utils.SETTING
import com.mikuac.shiro.annotation.GroupMessageHandler
import com.mikuac.shiro.annotation.common.Shiro
import com.mikuac.shiro.core.Bot
import com.mikuac.shiro.dto.event.message.GroupMessageEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component


/**
 * @author LoMu
 * Date 2024.07.04 10:22
 */

val log = KotlinLogging.logger { }

@Component
@Shiro
class GroupEventListen(
    private val bilibiliMessageCollect: BilibiliMessageCollect,
    private val bilibiliEventListen: BilibiliEventListen,
    private val bilibiliRequestData: BilibiliRequestData,
) {
    @GroupMessageHandler
    fun groupMsgListen(bot: Bot, groupMessageEvent: GroupMessageEvent) {
        val groupId = groupMessageEvent.groupId
        val message = groupMessageEvent.message
        if (SETTING.groupBvidListen) {
            if (!SETTING.bannedGroupBvidListen.contains(groupId)) {
                bilibiliEventListen.process(bot, groupId, message)
            }
        }

        if (groupMessageEvent.message.startsWith("最新动态")) {
            val uid = groupMessageEvent.message.split(" ")[1]
            val articleMessageCollect = bilibiliMessageCollect.articleMessageCollect(uid, 5)
            bot.sendGroupBilibiliarticle(
                articleMessageCollect.maxBy { it.id.toLong() },
                bilibiliRequestData,
                groupId
            )
        }
    }
}
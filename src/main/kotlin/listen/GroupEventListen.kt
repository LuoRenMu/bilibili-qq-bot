package cn.luorenmu.listen

import cn.luorenmu.action.listenProcess.BilibiliEventListen
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
    private val bilibiliEventListen: BilibiliEventListen,
) {
    @GroupMessageHandler
    fun groupMsgListen(bot: Bot, groupMessageEvent: GroupMessageEvent) {
        val groupId = groupMessageEvent.groupId
        val message = groupMessageEvent.message
        bilibiliEventListen.process(bot, groupId, message)
    }
}
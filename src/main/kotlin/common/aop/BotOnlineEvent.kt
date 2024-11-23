package cn.luorenmu.common.aop

import cn.luorenmu.action.listenProcess.BilibiliMessageCollect
import cn.luorenmu.common.utils.*
import cn.luorenmu.config.entity.groups
import com.mikuac.shiro.core.Bot
import io.github.oshai.kotlinlogging.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component
import java.io.File
import java.util.concurrent.TimeUnit


/**
 * @author LoMu
 * Date 2024.11.21 17:56
 *
 *  通过aop重写CoreEvent方法
 */

@Aspect
@Component
class BotOnlineEvent(
    private val bilibiliMessageCollect: BilibiliMessageCollect,
) {
    val log = KotlinLogging.logger {}

    @Pointcut("execution(* com.mikuac.shiro.core.CoreEvent.online(..))")
    fun ptOnline() {

    }

    @Pointcut("execution(* com.mikuac.shiro.core.CoreEvent.offline(..))")
    fun ptOffline() {

    }

    @Around("ptOnline()")
    fun aroundOnline(joinPoint: ProceedingJoinPoint) {
        val bot = joinPoint.args.first() as Bot
        log.info { "${bot.selfId} 已被LoMu-Bot接管" }
    }

    @Around("ptOffline()")
    fun aroundOffline(joinPoint: ProceedingJoinPoint) {
        val botId = joinPoint.args.first() as Long
        log.warn { "bot: $botId offline" }
    }

}
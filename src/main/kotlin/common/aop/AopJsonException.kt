package cn.luorenmu.common.aop

import cn.luorenmu.task.log
import com.alibaba.fastjson2.JSONException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2024.11.12 15:18
 */



@Aspect
@Component
class AopJsonException {
    val log = KotlinLogging.logger { }
    @Pointcut("execution(* cn.luorenmu.action.request.BilibiliRequestData.*(..))")
    fun pt() {
    }


    @Around("pt()")
    fun handleException(proceedingJoinPoint: ProceedingJoinPoint): Any? {
        try {
            return proceedingJoinPoint.proceed()
        } catch (e: JSONException) {
            log.warn { "bilibili bvid decode failed, This may be the video not exists or bvid is void \n ${e.stackTraceToString()}}" }
            return null
        }


    }
}
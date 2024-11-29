package cn.luorenmu.common.aop

import com.alibaba.fastjson2.JSONException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2024.11.12 15:18
 */


@Aspect
@Component
class AopException {
    val log = KotlinLogging.logger { }

    @Around("execution(* cn.luorenmu.action.request.BilibiliRequestData.*(..))")
    fun handleException(proceedingJoinPoint: ProceedingJoinPoint): Any? {
        try {
            return proceedingJoinPoint.proceed()
        } catch (e: JSONException) {
            log.warn { "bilibili bvid decode failed, This may be the video not exists or bvid is void \n ${e.stackTraceToString()}}" }
            return null
        }
    }
}
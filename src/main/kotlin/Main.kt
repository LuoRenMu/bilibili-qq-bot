package cn.luorenmu


import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.scheduling.annotation.EnableScheduling


/**
 * @author LoMu
 * Date 2024.07.04 8:14
 */

@EnableScheduling
@SpringBootApplication
@EnableAspectJAutoProxy
@ConfigurationPropertiesScan
@EnableConfigurationProperties
class MainApplication

fun main(args: Array<String>) {
    runApplication<MainApplication>(*args)
}


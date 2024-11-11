package cn.luorenmu

import cn.luorenmu.action.listenProcess.BilibiliMessageCollect
import cn.luorenmu.action.listenProcess.BilibiliRequestData
import com.mikuac.shiro.core.BotContainer
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/**
 * @author LoMu
 * Date 2024.11.02 18:50
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RequestTest(
    @Autowired val bilibiliMessageCollect: BilibiliMessageCollect,
    @Autowired val botContainer: BotContainer,
    @Autowired val bilibiliRequestData: BilibiliRequestData,
) {
    @Test
    fun tset() {
        val articleMessageCollect = bilibiliMessageCollect.articleMessageCollect("1955897084", 5)
        val article = articleMessageCollect.first { it.id == "997647072385564773" }
    }
}
package cn.luorenmu

import cn.luorenmu.action.listenProcess.BilibiliMessageCollect
import cn.luorenmu.action.listenProcess.BilibiliRequestData
import cn.luorenmu.common.extensions.sendGroupBilibiliarticle
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.common.utils.ShiroUtils
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
        val articleMessageCollect = bilibiliMessageCollect.articleMessageCollect("174501086", 5)
        val article = articleMessageCollect.firstOrNull { it.id == "998572165385158663" }
        val generateForwardMsg = ShiroUtils.generateForwardMsg(
            2371643417,
            "LomuBot",
            listOf(
                "https://i0.hdslb.com/bfs/new_dyn/4a73a53a5ca58bcfe96d6d90893832011955897084.jpg",
                "https://i0.hdslb.com/bfs/new_dyn/934120337e795fc1e12016b407bdad811955897084.jpg",
                "https://i0.hdslb.com/bfs/new_dyn/a11234e024d244299d576b174fbb9ba61955897084.gif",
                "https://i0.hdslb.com/bfs/new_dyn/fc94c4c325b1e5051014be4fe61650cd1955897084.gif"
            ).map { MsgUtils.builder().img(it).build() })
        println(article)
        botContainer.robots.values.first().sendGroupBilibiliarticle(article!!,bilibiliRequestData,646708986)
    }
}
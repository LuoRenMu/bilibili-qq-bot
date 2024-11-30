package cn.luorenmu

import cn.luorenmu.action.listenProcess.BilibiliMessageCollect
import cn.luorenmu.command.CustomizeCommandAllocator
import cn.luorenmu.command.entity.BotRole
import cn.luorenmu.command.entity.CommandSender
import cn.luorenmu.command.entity.CustomizeCommandInfo
import cn.luorenmu.command.entity.MessageType
import cn.luorenmu.common.utils.file.CustomizeCommandFileUtils.CUSTOMIZE_COMMAND
import cn.luorenmu.excetpion.NoFurtherProcessException
import com.mikuac.shiro.core.BotContainer
import io.github.oshai.kotlinlogging.KotlinLogging
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
    @Autowired val customizeCommandAllocator: CustomizeCommandAllocator,
) {
    val log = KotlinLogging.logger {}

    @Test
    fun test() {
        val commandSender = CommandSender(646708986, "123", 2842775752L, BotRole.OWNER, 123, "test", MessageType.GROUP)
        CUSTOMIZE_COMMAND.customizeCommandList.add(
            CustomizeCommandInfo(
                "权限不足",
                "^(test)$",
                returnMessageTemp = "执行成功",
                groupList = mutableListOf(646708986),
                role = "owner",
                probability = 0.3
            )
        )
        throw NoFurtherProcessException(commandSender, null)
    }
}
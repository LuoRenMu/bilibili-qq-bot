package cn.luorenmu.excetpion

import cn.luorenmu.command.entity.CommandSender

/**
 * @author LoMu
 * Date 2024.11.30 01:20
 */
open class LoMuBotException(
    open val commandSender: CommandSender,
    open val msg: String?,
) : RuntimeException()
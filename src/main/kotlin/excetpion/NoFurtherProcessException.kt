package cn.luorenmu.excetpion

import cn.luorenmu.command.entity.CommandSender

/**
 * @author LoMu
 * Date 2024.11.30 00:57
 */
class NoFurtherProcessException(
    override val commandSender: CommandSender,
    override val msg: String?,
) : LoMuBotException(commandSender, msg)
package com.zp4rker.disbot.console

/**
 * @author zp4rker
 *
 * Console command interface.
 */
interface ConsoleCommand {

    fun handleCommand(command: String)

}
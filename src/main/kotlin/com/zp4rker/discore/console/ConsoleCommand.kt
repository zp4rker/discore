package com.zp4rker.discore.console

/**
 * @author zp4rker
 *
 * Console command interface.
 */
interface ConsoleCommand {

    fun handleCommand(command: String, args: Array<String>)

}
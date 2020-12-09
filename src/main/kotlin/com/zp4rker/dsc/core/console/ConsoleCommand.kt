package com.zp4rker.dsc.core.console

/**
 * @author zp4rker
 *
 * Console command interface.
 */
interface ConsoleCommand {

    fun handleCommand(command: String)

}
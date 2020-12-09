package com.zp4rker.dsc.core.console.default

import com.zp4rker.dsc.core.console.Console
import com.zp4rker.dsc.core.console.ConsoleCommand

/**
 * @author zp4rker
 *
 * Default stop/shutdown command.
 */
class StopCommand : ConsoleCommand {

    override fun handleCommand(command: String) {
        Console.shutdown()
    }
}
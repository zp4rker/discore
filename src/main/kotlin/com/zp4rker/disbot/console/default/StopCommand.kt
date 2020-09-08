package com.zp4rker.disbot.console.default

import com.zp4rker.disbot.console.Console
import com.zp4rker.disbot.console.ConsoleCommand

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
package com.zp4rker.discore.console.default

import com.zp4rker.discore.console.Console
import com.zp4rker.discore.console.ConsoleCommand

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
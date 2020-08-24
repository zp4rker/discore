package com.zp4rker.disbot.console.default

import com.zp4rker.disbot.console.Console
import com.zp4rker.disbot.console.ConsoleCommand

class StopCommand : ConsoleCommand {

    override fun handleCommand(command: String) {
        Console.shutdown()
    }
}
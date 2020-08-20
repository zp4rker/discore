package com.zp4rker.discore.console.default

import com.zp4rker.discore.console.Console
import com.zp4rker.discore.console.ConsoleCommand
import com.zp4rker.discore.logger

class StopCommand : ConsoleCommand {

    override fun handleCommand(command: String) {
        logger.info("Stopping now...")
        // run stop code
        logger.info("Goodbye!")
        Console.shutdown()
    }
}
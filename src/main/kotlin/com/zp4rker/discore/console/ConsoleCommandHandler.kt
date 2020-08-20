package com.zp4rker.discore.console

import com.zp4rker.discore.logger

object ConsoleCommandHandler {

    fun handleCommand(command: String): Boolean {
        logger.debug("Ran command: $command")

        // stop command
        if (command == "stop") {
            logger.info("Stopping now...")
            // run stop code
            logger.info("Goodbye!")
            Console.shutdown()
            return true
        }

        return false
    }

}
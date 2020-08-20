package com.zp4rker.discore

import com.zp4rker.discore.console.Console
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val logger: Logger = LoggerFactory.getLogger("Discore")

class Main {

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            logger.info("Setting up...")
            // add setup code
            logger.info("Setup complete!")
            Console.start()
        }
    }

}
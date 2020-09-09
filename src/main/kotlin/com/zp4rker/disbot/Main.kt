package com.zp4rker.disbot

import com.zp4rker.disbot.console.Console
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

/**
 * @author zp4rker
 *
 * Bot main class.
 */
class Main {

    companion object {
        val logger: Logger = LoggerFactory.getLogger("Disbot")

        @JvmStatic fun main(args: Array<String>) {
            val inst = Main()
            logger.info("Starting Disbot v${inst.javaClass.`package`.implementationVersion} by zp4rker...")

            logger.info("Setting up...")
            // add setup code here
                // load modules
                // connect to jda
                // post jda-connect events
            logger.info("Setup complete!")
        }
    }

    init {
        Console.start()
    }

}
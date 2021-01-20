package com.zp4rker.discore.console

import com.zp4rker.discore.API
import com.zp4rker.discore.BOT
import com.zp4rker.discore.LOGGER
import com.zp4rker.discore.console.default.StopCommand

/**
 * @author zp4rker
 *
 * Thread for handling console commands.
 */
object Console : Thread() {
    init {
        // default commands
        ConsoleCommandHandler.registerCommand("stop", StopCommand(), "bye", "shutdown")

        // error handler
        setDefaultUncaughtExceptionHandler { _, exception ->
            LOGGER.error("Encountered an exception!", exception)
        }

        // shutdown hook
        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                shutdown()
            }
        })
    }

    private var isRunning = false

    override fun run() {
        var command: String = System.console().readLine().toLowerCase()
        while (isRunning) {
            if (!ConsoleCommandHandler.handleCommand(command)) LOGGER.warn("Unkown command \"$command\" - Type \"help\" for help.")

            if (isRunning) command = System.console().readLine().toLowerCase()
        }
    }

    override fun start() {
        isRunning = true
        super.start()
    }

    fun shutdown() {
        isRunning = false

        LOGGER.info("Stopping now...")
        BOT.quit()
        API.shutdownNow()
        LOGGER.info("Goodbye!")

        Runtime.getRuntime().halt(0)
    }

}
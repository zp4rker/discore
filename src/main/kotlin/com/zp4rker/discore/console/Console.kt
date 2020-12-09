package com.zp4rker.discore.console

import com.zp4rker.discore.API
import com.zp4rker.discore.BOT
import com.zp4rker.discore.LOGGER
import com.zp4rker.discore.console.default.StopCommand
import org.jline.reader.LineReaderBuilder
import org.jline.reader.UserInterruptException
import org.jline.terminal.TerminalBuilder

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
            if (exception is UserInterruptException) {
                shutdown()
            } else {
                LOGGER.error("Encountered an exception!", exception)
            }
        }

        // shutdown hook
        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                shutdown()
            }
        })
    }

    private var isRunning = false
    private val reader = LineReaderBuilder.builder().appName("Disbot").terminal(TerminalBuilder.terminal()).build()

    override fun run() {
        var command: String? = reader.readLine()?.toLowerCase()
        while (command != null && isRunning) {
            if (!ConsoleCommandHandler.handleCommand(command)) LOGGER.warn("Unkown command. Type \"help\" for help.")

            if (isRunning) command = reader.readLine()?.toLowerCase()
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
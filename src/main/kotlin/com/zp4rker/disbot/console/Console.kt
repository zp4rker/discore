package com.zp4rker.disbot.console

import com.zp4rker.disbot.API
import com.zp4rker.disbot.console.default.StopCommand
import com.zp4rker.disbot.LOGGER
import net.dv8tion.jda.api.JDA
import org.jline.reader.LineReaderBuilder
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
            LOGGER.error("Encountered an exception!", exception)
        }

        // shutdown hook
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
        API.shutdownNow()
        LOGGER.info("Goodbye!")
    }

}
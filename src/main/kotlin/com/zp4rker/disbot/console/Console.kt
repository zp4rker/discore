package com.zp4rker.disbot.console

import com.zp4rker.disbot.console.default.StopCommand
import org.jline.reader.LineReaderBuilder
import org.jline.terminal.TerminalBuilder
import org.slf4j.LoggerFactory

/**
 * @author zp4rker
 *
 * Thread for handling console commands.
 */
object Console : Thread() {

    private val logger = LoggerFactory.getLogger("Disbot")

    init {
        // default commands
        ConsoleCommandHandler.registerCommand("stop", StopCommand(), "bye", "shutdown")

        // error handler
        setDefaultUncaughtExceptionHandler { _, exception ->
            logger.error("Encountered an exception!", exception)
        }

        // shutdown hook

    }

    private var isRunning = false
    private val reader = LineReaderBuilder.builder().appName("Disbot").terminal(TerminalBuilder.terminal()).build()

    override fun run() {
        var command: String? = reader.readLine()?.toLowerCase()
        while (command != null && isRunning) {
            if (!ConsoleCommandHandler.handleCommand(command)) logger.warn("Unkown command. Type \"help\" for help.")

            if (isRunning) command = reader.readLine()?.toLowerCase()
        }
    }

    override fun start() {
        isRunning = true
        super.start()
    }

    fun shutdown() {
        isRunning = false

        logger.info("Stopping now...")
        // run stop code
        logger.info("Goodbye!")
    }

}
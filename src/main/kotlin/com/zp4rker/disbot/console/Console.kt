package com.zp4rker.disbot.console

import com.zp4rker.disbot.Main
import com.zp4rker.disbot.console.default.StopCommand
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
            Main.logger.error("Encountered an exception!", exception)
        }

        // shutdown hook

    }

    private var isRunning = false
    private val reader = LineReaderBuilder.builder().appName("Disbot").terminal(TerminalBuilder.terminal()).build()

    override fun run() {
        var command: String? = reader.readLine()?.toLowerCase()
        while (command != null && isRunning) {
            if (!ConsoleCommandHandler.handleCommand(command)) Main.logger.warn("Unkown command. Type \"help\" for help.")

            if (isRunning) command = reader.readLine()?.toLowerCase()
        }
    }

    override fun start() {
        isRunning = true
        super.start()
    }

    fun shutdown() {
        isRunning = false

        Main.logger.info("Stopping now...")
        // run stop code
        Main.logger.info("Goodbye!")
    }

}
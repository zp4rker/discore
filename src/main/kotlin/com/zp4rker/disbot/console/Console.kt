package com.zp4rker.disbot.console

import com.zp4rker.disbot.console.default.StopCommand
import com.zp4rker.disbot.globalLogger
import net.dv8tion.jda.api.JDA
import org.jline.reader.LineReaderBuilder
import org.jline.terminal.TerminalBuilder

/**
 * @author zp4rker
 *
 * Thread for handling console commands.
 */
object Console : Thread() {
    
    var jda: JDA? = null

    init {
        // default commands
        ConsoleCommandHandler.registerCommand("stop", StopCommand(), "bye", "shutdown")

        // error handler
        setDefaultUncaughtExceptionHandler { _, exception ->
            globalLogger.error("Encountered an exception!", exception)
        }

        // shutdown hook
    }

    private var isRunning = false
    private val reader = LineReaderBuilder.builder().appName("Disbot").terminal(TerminalBuilder.terminal()).build()

    override fun run() {
        var command: String? = reader.readLine()?.toLowerCase()
        while (command != null && isRunning) {
            if (!ConsoleCommandHandler.handleCommand(command)) globalLogger.warn("Unkown command. Type \"help\" for help.")

            if (isRunning) command = reader.readLine()?.toLowerCase()
        }
    }

    override fun start() {
        isRunning = true
        super.start()
    }

    fun shutdown() {
        isRunning = false

        globalLogger.info("Stopping now...")
        jda?.shutdownNow()
        globalLogger.info("Goodbye!")
    }

}
package com.zp4rker.discore.console

import com.zp4rker.discore.console.default.StopCommand
import com.zp4rker.discore.logger
import org.jline.reader.LineReaderBuilder
import org.jline.terminal.TerminalBuilder

object Console : Thread() {

    init {
        // default commands
        ConsoleCommandHandler.registerCommand("stop", StopCommand(), "bye")
    }

    private var isRunning = false
    private val reader = LineReaderBuilder.builder().appName("Discore").terminal(TerminalBuilder.terminal()).build()

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
    }

}
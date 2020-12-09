package com.zp4rker.discore.console

import org.slf4j.LoggerFactory

/**
 * @author zp4rker
 *
 * Console command handler.
 */
object ConsoleCommandHandler {

    private val logger = LoggerFactory.getLogger("Disbot")

    private val commands: MutableMap<String, ConsoleCommand> = mutableMapOf()

    fun handleCommand(command: String): Boolean {
        logger.debug("Ran command: $command")

        if (commands.containsKey(command)) {
            commands[command]?.handleCommand(command)
            return true
        }

        return false
    }

    fun registerCommand(label: String, handler: ConsoleCommand, vararg aliases: String) {
        // base command
        if (commands.containsKey(label)) throw IllegalStateException("Command with label \"$label\" already exists!")
        commands[label] = handler
        // aliases
        aliases.forEach {
            if (commands.containsKey(it)) throw java.lang.IllegalStateException("Command with alias \"$it\" already exists!")
            commands[it] = handler
        }
    }

}
package com.zp4rker.dsc.core

import com.zp4rker.disbot.command.Command
import com.zp4rker.disbot.command.CommandHandler
import com.zp4rker.disbot.console.Console
import com.zp4rker.disbot.extenstions.separator
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.hooks.InterfacedEventManager
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author zp4rker
 */

class Bot {

    var name: String = "Disbot"
        set(value) {
            field = value
            logger = LoggerFactory.getLogger(name)
        }

    var version: String = "1.0.0"

    var token: String = "empty"
        set(value) {
            field = value
            jdaBuilder = JDABuilder.createDefault(value, GatewayIntent.getIntents(intents))
        }
    var prefix: String = "/"

    var logger: Logger = LoggerFactory.getLogger(name)

    private lateinit var cmdHandler: CommandHandler
    var helpCommandEnabled = true
    var commands: Array<Command> = arrayOf()

    var activity: Activity? = null
    var intents: Int = GatewayIntent.DEFAULT
        set(value) {
            field = value
            jdaBuilder = JDABuilder.createDefault(token, GatewayIntent.getIntents(value))
        }
    var cacheEnabled = false

    var quit: () -> Unit = {}

    private lateinit var jdaBuilder: JDABuilder

    fun build() {
        val disbotVersion = MANIFEST.getValue("Disbot-Version")
        val jdaVersion = MANIFEST.getValue("JDA-Version")

        LOGGER.separator()
        LOGGER.info("Starting $name v$version")
        LOGGER.info("Powered by Disbot v${disbotVersion}")
        LOGGER.info("Created by zp4rker#3333")
        LOGGER.info("Utilising JDA v${jdaVersion}")
        LOGGER.separator()

        API = jdaBuilder.apply {
            if (activity != null) setActivity(activity)

            if (cacheEnabled) enableCache(CacheFlag.values().toList())
            else disableCache(CacheFlag.values().toList())

            setEventManager(InterfacedEventManager())
        }.build()

        cmdHandler = CommandHandler(prefix)
        if (helpCommandEnabled) cmdHandler.registerHelpCommand()
        cmdHandler.registerCommands(*commands)

        BOT = this
    }

    fun addCommands(vararg commands: Command) = cmdHandler.registerCommands(*commands)
    fun addEventListeners(vararg listeners: EventListener) = jdaBuilder.addEventListeners(*listeners)

}

fun bot(builder: Bot.() -> Unit): Bot {
    Console.start()
    return Bot().also(builder).also { it.build() }
}
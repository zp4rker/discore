package com.zp4rker.discore

import com.zp4rker.discore.command.Command
import com.zp4rker.discore.command.CommandHandler
import com.zp4rker.discore.console.Console
import com.zp4rker.discore.extenstions.separator
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
    var helpCommandEnabled = false
    var commands: List<Command> = listOf()

    var activity: Activity? = null
    var intents: Int = GatewayIntent.DEFAULT
        set(value) {
            field = value
            jdaBuilder = JDABuilder.createDefault(token, GatewayIntent.getIntents(value))
        }
    var cache: List<CacheFlag> = listOf()

    var quit: () -> Unit = {}

    private lateinit var jdaBuilder: JDABuilder

    fun build() {
        val disbotVersion = MANIFEST.getValue("Disbot-Version")
        val jdaVersion = MANIFEST.getValue("JDA-Version")

        LOGGER.separator()
        LOGGER.info("Starting $name v$version")
        LOGGER.info("Powered by Discore v${disbotVersion}")
        LOGGER.info("Created by zp4rker#3333")
        LOGGER.info("Utilising JDA v${jdaVersion}")
        LOGGER.separator()

        API = jdaBuilder.apply {
            if (activity != null) setActivity(activity)

            if (cache.isNotEmpty()) enableCache(cache)
            else disableCache(CacheFlag.values().toList())

            setEventManager(InterfacedEventManager())
        }.build()

        cmdHandler = CommandHandler(prefix)
        if (helpCommandEnabled) cmdHandler.registerHelpCommand()
        cmdHandler.registerCommands(*commands.toTypedArray())

        BOT = this
    }

    fun addCommands(vararg commands: Command) = cmdHandler.registerCommands(*commands)
    fun addEventListeners(vararg listeners: EventListener) = jdaBuilder.addEventListeners(*listeners)

}

fun bot(builder: Bot.() -> Unit): Bot {
    Console.start()
    return Bot().also(builder).also { it.build() }
}
package com.zp4rker.disbot

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
    var version: String = "1.0.0"

    lateinit var token: String
    lateinit var prefix: String

    lateinit var logger: Logger

    private val cmdHandler = CommandHandler(prefix)
    var helpCommandEnabled = true

    var activity: Activity? = null
    var intents: Int = GatewayIntent.DEFAULT
    var cacheEnabled = false

    var quit: () -> Unit = {}

    private val jdaBuilder = JDABuilder.createDefault(token, GatewayIntent.getIntents(intents))

    fun build() {
        val disbotVersion = MANIFEST.getValue("Disbot-Version")
        val jdaVersion = MANIFEST.getValue("JDA-Version")

        LOGGER.separator()
        LOGGER.info("Starting $name v$version")
        LOGGER.info("Powered by Disbot v${disbotVersion}, created by zp4rker")
        LOGGER.info("Utilising JDA v${jdaVersion}")
        LOGGER.separator()

        logger = LoggerFactory.getLogger(name)

        API = jdaBuilder.apply {
            if (activity != null) setActivity(activity)

            if (cacheEnabled) enableCache(CacheFlag.values().toList())
            else disableCache(CacheFlag.values().toList())

            setEventManager(InterfacedEventManager())
        }.build()

        if (helpCommandEnabled) cmdHandler.registerHelpCommand()

        NEWBOT = this
    }

    fun addCommands(vararg commands: Command) = cmdHandler.registerCommands(*commands)
    fun addEventListeners(vararg listeners: EventListener) = jdaBuilder.addEventListeners(*listeners)

}

fun bot(builder: Bot.() -> Unit): Bot {
    Console.start()
    return Bot().also(builder).also { it.build() }
}
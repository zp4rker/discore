package com.zp4rker.discore

import com.zp4rker.discore.command.Command
import com.zp4rker.discore.command.CommandHandler
import com.zp4rker.discore.console.Console
import com.zp4rker.discore.console.log
import com.zp4rker.discore.console.separator
import com.zp4rker.discore.util.linedName
import io.leego.banana.BananaUtils
import io.leego.banana.Font
import io.leego.banana.Layout
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

    var name: String = "Discore"
        set(value) {
            field = value
            logger = LoggerFactory.getLogger(name)
        }

    var version: String = Bot::class.java.`package`.implementationVersion

    var author: String = "zp4rker#3333"

    var token: String = "empty"
        set(value) {
            field = value
            jdaBuilder = JDABuilder.create(value, GatewayIntent.getIntents(intents))
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
            jdaBuilder = JDABuilder.create(token, GatewayIntent.getIntents(value))
        }
    var cache: List<CacheFlag> = listOf()

    var quit: () -> Unit = {}

    private lateinit var jdaBuilder: JDABuilder

    fun build() {
        val discoreVersion = MANIFEST.getValue("Discore-Version")
        val jdaVersion = MANIFEST.getValue("JDA-Version")

        log("\n${BananaUtils.bananaify(linedName(name), Font.BIG_MONEY_NW, Layout.SMUSH_R, Layout.SMUSH_R).trimEnd()}")
        log(BananaUtils.bananaify("v$version", Font.RECTANGLES).trimEnd() + "\tby $author\n")

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
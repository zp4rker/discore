package com.zp4rker.discore

import com.zp4rker.discore.command.Command
import com.zp4rker.discore.command.CommandHandler
import com.zp4rker.discore.console.Console
import com.zp4rker.discore.log.blankLine
import com.zp4rker.discore.log.info
import com.zp4rker.discore.extenstions.event.on
import com.zp4rker.discore.util.linedName
import io.leego.banana.BananaUtils
import io.leego.banana.Font
import io.leego.banana.Layout
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.hooks.InterfacedEventManager
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.fusesource.jansi.Ansi

/**
 * @author zp4rker
 */

class Bot {

    var name: String = "Discore"

    var version: String = Bot::class.java.`package`.implementationVersion

    var author: String = "zp4rker#3333"

    var token: String = "empty"
        set(value) {
            field = value
            jdaBuilder = JDABuilder.create(value, GatewayIntent.getIntents(intents))
        }
    var prefix: String = "/"

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

    var debug: Boolean = false

    private lateinit var jdaBuilder: JDABuilder

    fun build() {
        LOGGER.info("\n${BananaUtils.bananaify(linedName(name), Font.BIG_MONEY_NW, Layout.SMUSH_R, Layout.SMUSH_R).trimEnd()}")
        LOGGER.info(BananaUtils.bananaify("v$version", Font.RECTANGLES).trimEnd() + "\tby $author\n")

        val discoreVersion = MANIFEST.getValue("Discore-Version")
        val jdaVersion = MANIFEST.getValue("JDA-Version")

        LOGGER.info(Ansi.ansi().a("Powered by Discore ").fgBrightYellow().a("v$discoreVersion"))
        LOGGER.info(Ansi.ansi().a("Built on JDA ").fgBrightYellow().a("v$jdaVersion"))
        LOGGER.blankLine()

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

        API.on<ReadyEvent> {
            val self = API.selfUser
            LOGGER.info("Authenticated as ${self.asTag}")
            LOGGER.info("${API.guilds.size} total guild(s)")
            LOGGER.info("${API.textChannels.size} total textchannel(s)")
            LOGGER.info("${API.roles.size} total role(s)")
            LOGGER.info("${API.users.size} total user(s)")
            LOGGER.blankLine()
        }
    }

    fun addCommands(vararg commands: Command) = cmdHandler.registerCommands(*commands)
    fun addEventListeners(vararg listeners: EventListener) = jdaBuilder.addEventListeners(*listeners)

}

fun bot(builder: Bot.() -> Unit): Bot {
    Console.start()
    return Bot().also(builder).also { it.build() }
}
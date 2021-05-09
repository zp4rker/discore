package com.zp4rker.discore

import com.zp4rker.discore.command.Command
import com.zp4rker.discore.command.CommandHandler
import com.zp4rker.discore.console.Console
import com.zp4rker.discore.event.on
import com.zp4rker.discore.log.blankLine
import com.zp4rker.discore.log.info
import com.zp4rker.discore.log.initLogBackend
import io.leego.banana.BananaUtils
import io.leego.banana.Font
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.JDAInfo
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.hooks.InterfacedEventManager
import org.fusesource.jansi.Ansi

/**
 * @author zp4rker
 */

class Bot {

    var name: String = "discore"

    var version: String = Bot::class.java.`package`.implementationVersion

    var author: String = "zp4rker#3333"

    var token: String = "empty"
        set(value) {
            field = value
            jdaBuilder = JDABuilder.createDefault(value)
        }
    var prefix: String = "/"

    private lateinit var cmdHandler: CommandHandler
    var helpCommandEnabled = false
    var commands: List<Command>

    var activity: Activity? = null

    var quit: () -> Unit = {}

    lateinit var jdaBuilder: JDABuilder

    fun addCommands(vararg commands: Command) = cmdHandler.registerCommands(*commands)

    init {
        initLogBackend()
        commands = findCommands()
    }

    fun build() {
        LOGGER.info("${BananaUtils.bananaify(name.lowercase(), Font.RECTANGLES).trimEnd()}${" ".repeat(4)}v$version")
        LOGGER.blankLine()

        LOGGER.info("Created by zp4rker#3333")
        LOGGER.info(Ansi.ansi().a("Powered by Discore ").fgBrightYellow().a("v$DISCORE_VERSION"))
        LOGGER.info(Ansi.ansi().a("Built on JDA ").fgBrightYellow().a("v${JDAInfo.VERSION}"))
        LOGGER.blankLine()

        API = jdaBuilder.apply {
            if (activity != null) setActivity(activity)

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

}

fun bot(builder: Bot.() -> Unit): Bot {
    Console.start()
    return Bot().also(builder).also { it.build() }
}
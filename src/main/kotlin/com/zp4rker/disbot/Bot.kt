package com.zp4rker.disbot

import com.zp4rker.disbot.command.Command
import com.zp4rker.disbot.command.CommandHandler
import com.zp4rker.disbot.console.Console
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author zp4rker
 */
class Bot(name: String, prefix: String) {

    private val cmdHandler: CommandHandler = CommandHandler(prefix)
    val logger: Logger = LoggerFactory.getLogger(name)

    companion object {
        fun create(specs: BotBuilder.() -> Unit): Bot {
            Console.start()

            val builder = BotBuilder()
            specs.invoke(builder)

            val bot = Bot(builder.name, builder.prefix)
            builder.commands.forEach(bot.cmdHandler::registerCommands)

            Console.jda = JDABuilder.createDefault(builder.token, GatewayIntent.getIntents(builder.intents)).apply {
                if (builder.activity != null) setActivity(builder.activity)

                if (builder.cacheEnabled) enableCache(CacheFlag.values().toList())
                else disableCache(CacheFlag.values().toList())

                addEventListeners(bot.cmdHandler)
            }.build().awaitStatus(JDA.Status.CONNECTED)

            return bot
        }
    }

    class BotBuilder {
        var name: String = "Disbot"

        lateinit var token: String
        lateinit var prefix: String

        var commands: List<Command> = emptyList()

        var activity: Activity? = null
        var intents: Int = GatewayIntent.DEFAULT
        var cacheEnabled = false
    }
}
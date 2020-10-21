package com.zp4rker.disbot

import com.zp4rker.disbot.command.CommandHandler
import com.zp4rker.disbot.config.TomlFile
import com.zp4rker.disbot.console.Console
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author zp4rker
 */
class Bot {

    val config = TomlFile("config.toml", Bot::class.java.getResource("/config.toml").readText())
    val cmdHandler: CommandHandler = CommandHandler(config.get("prefix"))
    val logger: Logger

    val name: String
    val version: String
    val description: String?
    val author: String?

    init {
        val botInfo = TomlFile(Bot::class.java.getResourceAsStream("/bot.toml"))

        name = botInfo.get("name")
        version = botInfo.get("version")
        description = botInfo.get("description")
        author = botInfo.get("author")

        logger = LoggerFactory.getLogger(name)
    }

    fun create(specs: BotBuilder.() -> Unit): Bot {
        // setup phase - separate function?
        Console.start()

        val builder = BotBuilder()
        specs.invoke(builder)

        JDABuilder.createDefault(config.get("token"), builder.intents).apply {
            setActivity(builder.activity)

            if (builder.cacheEnabled) enableCache(CacheFlag.values().toList())

            addEventListeners(cmdHandler)
        }.build()

        return this
    }

    class BotBuilder {
        var activity: Activity = Activity.playing("")
        var intents: List<GatewayIntent> = GatewayIntent.values().toList()
        var cacheEnabled = false
    }
}
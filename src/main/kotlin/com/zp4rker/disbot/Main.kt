package com.zp4rker.disbot

import com.github.jezza.TomlTable
import com.zp4rker.disbot.config.TomlFile
import com.zp4rker.disbot.console.Console
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.hooks.AnnotatedEventManager
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author zp4rker
 *
 * Bot main class.
 */
class Main {

    companion object {
        val logger: Logger = LoggerFactory.getLogger("Disbot")
        lateinit var jda: JDA

        private val config = TomlFile("config.toml", Main::class.java.getResource("/config.toml").readText())

        @JvmStatic fun main(args: Array<String>) {
            logger.info("Starting Disbot v${Main().javaClass.`package`.implementationVersion} by zp4rker...")

            logger.info("Setting up...")
            jda = buildJDA() // connect to jda
            // post jda-connect events
            logger.info("Setup complete!")
        }

        private fun buildJDA(): JDA {
            return JDABuilder.createDefault(config.get<String>("token"), GatewayIntent.values().toList()).apply {
                if (config.get<Boolean>("cacheEnabled") != false) {
                    enableCache(CacheFlag.values().toList())
                }
                setEventManager(AnnotatedEventManager())
                config.get<TomlTable>("activity")?.also {
                    val type = Activity.ActivityType.valueOf(config.get<String>("activity.type")!!)
                    val message = config.get<String>("message")!!
                    val url = config.get<String>("url")!!

                    if (type == Activity.ActivityType.STREAMING) setActivity(Activity.streaming(message, url))
                    else setActivity(Activity.of(type, message))
                }
            }.build()
        }
    }

    init {
        Console.start()
    }

}
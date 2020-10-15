package com.zp4rker.disbot

import com.github.jezza.TomlTable
import com.zp4rker.disbot.config.TomlFile
import com.zp4rker.disbot.console.Console
import com.zp4rker.disbot.module.ModuleLoader
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.hooks.AnnotatedEventManager
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

/**
 * @author zp4rker
 *
 * Bot main class.
 */
class Main {

    companion object {
        val logger: Logger = LoggerFactory.getLogger("Disbot")
        private val modLoader: ModuleLoader = ModuleLoader()

        private val config = TomlFile("bot.toml", Main::class.java.getResource("/bot.toml").readText())

        @JvmStatic fun main(args: Array<String>) {
            val inst = Main()
            logger.info("Starting Disbot v${inst.javaClass.`package`.implementationVersion} by zp4rker...")

            logger.info("Setting up...")
            // add setup code here
            modLoader.loadModules() // load modules
            JDABuilder.createDefault(config.get<String>("token"), GatewayIntent.values().toList()).build() // connect to jda
                // post jda-connect events
            logger.info("Setup complete!")
        }

        private fun buildJDA() {
            with(JDABuilder.createDefault(config.get<String>("token"), GatewayIntent.values().toList())) {
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
            }
        }
    }

    init {
        Console.start()
    }

}
package com.zp4rker.discore.api

import com.zp4rker.discore.config.JsonFile
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.system.exitProcess

class BotInfo {

    private var data: JsonFile? = null

    private val main: String
        get() = data?.optString("main", "notset") ?: "notset"

    val name: String
        get() = data?.optString("name", "notset") ?: "notset"

    val version: String
        get() = data?.optString("version", "notset") ?: "notset"

    val jdaVersion: String
        get() = data?.optString("jda-version", "notset") ?: "4.2.0_192"

    val author: String
        get() = data?.optString("author", "notset") ?: "notset"

    val modules: List<String>
        get() = data?.optJSONArray("modules")?.map { it as String }?.toList() ?: emptyList()

    private fun isValid(): Boolean = arrayOf("name", "version", "main", "author", "jda-version").all(data!!::has)

    fun setup() {
        val logger = LoggerFactory.getLogger("Discore")

        Bot::class.java.getResource("/bot.json")?.let { data = JsonFile.load(File(it.toURI())) } ?: kotlin.run {
            logger.error("bot.json missing! Please create a valid bot.json.")
            logger.warn("Shutting down...")
            exitProcess(0)
        }

        if (!isValid()) {
            logger.error("bot.json invalid! Please use a valid bot.json.")
            logger.error("Shutting down...")
            exitProcess(0)
        } else {
            try {
                val inst = Class.forName(main).getDeclaredConstructor().newInstance() as Bot
                inst.run()
            } catch (e: ClassNotFoundException) {
                logger.error("Unabled to find main class for $name v$version! Shutting down...")
                exitProcess(0)
            }
        }
    }

}
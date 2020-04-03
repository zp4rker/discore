package com.zp4rker.discore.api

import com.zp4rker.discore.config.JsonFile
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

class BotInfo {

    private val data: JsonFile = JsonFile(Bot::class.java.getResource("/bot.json").readText())

    private val main: String
        get() = data.optString("main", "notset")

    val name: String
        get() = data.optString("name", "notset")

    val version: String
        get() = data.optString("version", "notset")

    val jdaVersion: String
        get() = data.optString("jda-version", "notset")

    val author: String
        get() = data.optString("author", "notset")

    val modules: List<String>
        get() = data.optJSONArray("modules")?.map { it as String }?.toList() ?: emptyList()

    private fun isValid(): Boolean = arrayOf("name", "version", "main", "author", "jda-version").all(data::has)

    fun setup() {
        val logger = LoggerFactory.getLogger("BotLoader")

        if (!isValid()) {
            logger.error("bot.json invalid! Shutting down...")
            exitProcess(0)
        } else {
            try {
                val inst = Class.forName(main).newInstance() as Bot
                inst.run()
            } catch (e: ClassNotFoundException) {
                logger.error("Unabled to find main class for $name v$version! Shutting down...")
                exitProcess(0)
            }
        }
    }

}
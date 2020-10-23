package com.zp4rker.disbot.config

import com.github.jezza.TomlTable
import com.zp4rker.disbot.storage.TomlFile
import net.dv8tion.jda.api.entities.Activity
import java.io.File

/**
 * @author zp4rker
 */
class BotConfig {

    private val tomlFile = TomlFile("config.toml", BotConfig::class.java.getResource("/bot.toml").readText())

    init {
        val file = File("config.toml")
        if (file.readText().isBlank()) {
            tomlFile.save(file)
        }
    }

    val name: String = tomlFile.get("name")
    val token: String = tomlFile.get("token")
    val prefix: String = tomlFile.get("prefix")

    val cacheEnabled: Boolean = tomlFile.get("cacheEnabeld")

    val activity: Activity = tomlFile.get<TomlTable>("activity").run {
        val type = Activity.ActivityType.valueOf(get("type") as String)
        val message = get("message") as String
        val url = get("url") as String
        if (url.isEmpty()) Activity.of(type, message) else Activity.of(type, message, url)
    }

}
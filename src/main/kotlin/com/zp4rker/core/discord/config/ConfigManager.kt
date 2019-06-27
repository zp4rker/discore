package com.zp4rker.core.discord.config

import java.io.File

class ConfigManager {

    companion object {
        fun loadConfig(filename: String): Config {
            val dir = File(ConfigManager::class.java.protectionDomain.codeSource.location.toURI()).parentFile
            val file = File(dir, filename)
            val content = file.readText()

            return if (content.isEmpty()) Config("{}", file) else Config(content, file)
        }
    }

}
package com.zp4rker.core.discord.config

import org.json.JSONObject
import java.io.File

class Config(content: String, private val file: File): JSONObject(content) {

    fun save() {
        file.writeText(this.toString(2))
    }

    companion object {
        fun load(filename: String, sameDir: Boolean = false): Config {
            val dir = File(Config::class.java.protectionDomain.codeSource.location.toURI()).parentFile
            val file = if (sameDir) File(dir, filename) else File(filename)
            val content = file.readText()

            return if (content.isEmpty()) Config("{}", file) else Config(content, file)
        }
    }

}
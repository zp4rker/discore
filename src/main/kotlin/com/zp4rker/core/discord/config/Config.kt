package com.zp4rker.core.discord.config

import org.json.JSONObject
import java.io.File

class Config(content: String, private val file: File): JSONObject(content) {

    fun save() {
        file.writeText(this.toString(2))
    }

    companion object {
        fun load(filename: String, sameDir: Boolean = false): Config {
            val file = getFile(filename, sameDir)
            return load(file)
        }

        fun loadOrDefault(filename: String, sameDir: Boolean = false, default: String = filename): Config {
            val file = getFile(filename, sameDir)

            if (file.exists()) return load(file)

            file.createNewFile()
            file.writeText(readDefaultFromResource(default))
            return load(file)
        }

        private fun load(file: File): Config {
            val content = file.readText()

            return if (content.isEmpty()) Config("{}", file) else Config(content, file)
        }

        private fun getFile(filename: String, sameDir: Boolean = false): File {
            val dir = File(Config::class.java.protectionDomain.codeSource.location.toURI()).parentFile
            return if (sameDir) File(dir, filename) else File(filename)
        }

        private fun readDefaultFromResource(filename: String) = object {}.javaClass.getResource("/$filename").readText()
    }

}
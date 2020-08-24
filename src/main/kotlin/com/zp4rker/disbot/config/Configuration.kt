package com.zp4rker.disbot.config

import org.tomlj.Toml
import org.tomlj.TomlTable
import java.io.File

class Configuration(private val fileName: String) {

    val toml: TomlTable

    init {
        val file = File(fileName).also {
            if (!it.exists()) {
                if (it.parentFile != null) it.parentFile.mkdirs()
                it.createNewFile()
            }
        }

        toml = Toml.parse(file.path)
    }

}
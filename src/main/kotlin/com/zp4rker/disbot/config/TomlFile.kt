package com.zp4rker.disbot.config

import com.github.jezza.Toml
import com.github.jezza.TomlTable
import java.io.File
import java.io.InputStream

class TomlFile {

    constructor(input: InputStream) {
        toml = if (input.reader().readText().isEmpty()) TomlTable()
        else Toml.from(input)
    }

    constructor(fileName: String) {
        File(fileName).run {
            if (!exists()) createNewFile()
            toml = if (readText().isEmpty()) TomlTable()
            else Toml.from(inputStream())
        }
    }

    constructor(fileName: String, defaults: String) {
        File(fileName).run {
            if (!exists()) createNewFile()
            toml = if (readText().isEmpty()) Toml.from(defaults.reader())
            else Toml.from(inputStream())
        }
    }

    fun write(file: File) {
        if (!file.exists()) file.createNewFile()
        // TODO: need to replace `toml.toString()` with actual toml format
        file.writeText(toml.toString())
    }

    private val toml: TomlTable

    operator fun get(key: String): Any? = toml[key]

    operator fun get(key: String, default: Any): Any = toml[key] ?: default

    operator fun set(key: String, value: Any) = toml.put(key, value)

    fun size() = toml.size()

}
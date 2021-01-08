package com.zp4rker.discore.util

import com.charleskorn.kaml.Yaml
import com.zp4rker.discore.Bot
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import java.io.File
import kotlin.reflect.full.*

/**
 * @author zp4rker
 */

inline fun <reified T> Yaml.encode(value: T): String {
    return this.encodeToString(value).replace(Regex(".*: null[\\n]?"), "")
}

inline fun <reified T> loadYamlOrDefault(file: File): T {
    if (file.length() > 0) return Yaml.default.decodeFromString(file.readText())

    if (!file.exists()) file.createNewFile()

    with(Bot::class.java.getResourceAsStream("/${file.name}")) {
        if (this == null) {
            val default = T::class.companionObject!!.declaredMemberProperties.find { it.name == "default" }!!.getter.call(T::class.companionObjectInstance)
            return (default as T).also { file.writeText(Yaml.default.encode(it)) }
        }
        file.writeText(use { it.reader().use { r -> r.readText() } })
        return Yaml.default.decodeFromString(file.readText())
    }
}
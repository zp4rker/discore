package com.zp4rker.disbot.config

import com.github.jezza.Toml
import com.github.jezza.TomlArray
import com.github.jezza.TomlTable
import java.io.File
import java.io.InputStream
import java.lang.StringBuilder
import java.time.temporal.TemporalAccessor
import java.time.temporal.TemporalQueries

/**
 * @author zp4rker
 *
 * Class for handling TOML (*.toml) files.
 */
class TomlFile {

    private val toml: TomlTable

    constructor(input: InputStream) {
        val string = input.reader().readText()
        toml = if (string.isEmpty()) TomlTable()
        else Toml.from(string.reader())
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
        val sb = StringBuilder().also { export(it, toml) }
        file.writeText(sb.toString().trim())
    }

    private fun export(sb: StringBuilder, obj: Any) {
        if (obj !is TomlArray) sb.append("\n")

        if (obj is TomlTable) {
            for (entry in obj.entrySet()) {
                if (entry.value is TomlTable) {
                    sb.append("\n").append("[").append(entry.key).append("]")
                    export(sb, entry.value)
                } else {
                    sb.append(entry.key).append(" = ")
                    when (val value = entry.value) {
                        is TomlArray -> export(sb, value)
                        is String -> sb.append("\"").append(value).append("\"")
                        is TemporalAccessor -> sb.append(value.query(TemporalQueries.chronology()))
                                .append(":").append(value.query(TemporalQueries.localDate()))
                                .append(":").append(value.query(TemporalQueries.localTime()))
                                .append(":").append(value.query(TemporalQueries.precision()))
                                .append(":").append(value.query(TemporalQueries.zone()))
                        else -> sb.append(value)
                    }
                    sb.append("\n")
                }
            }
            sb.append("\n")
        } else if (obj is TomlArray) {
            sb.append("[")
            obj.forEachIndexed { i, value ->
                when (value) {
                    is TomlArray -> export(sb, value)
                    is String -> sb.append("\"").append(value).append("\"")
                    is TemporalAccessor -> sb.append(value.query(TemporalQueries.chronology()))
                            .append(":").append(value.query(TemporalQueries.localDate()))
                            .append(":").append(value.query(TemporalQueries.localTime()))
                            .append(":").append(value.query(TemporalQueries.precision()))
                            .append(":").append(value.query(TemporalQueries.zone()))
                    else -> sb.append(value)
                }

                if (i + 1 < obj.size) sb.append(", ")
            }
            sb.append("]")
        }
    }

    fun <T> get(key: String): T? = toml[key] as T?

    fun <T> get(key: String, default: T): T = get<T>(key) ?: default

    operator fun set(key: String, value: Any): Any = toml.put(key, value)

    fun size() = toml.size()

}
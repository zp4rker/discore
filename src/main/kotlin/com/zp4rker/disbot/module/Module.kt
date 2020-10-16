package com.zp4rker.disbot.module

import com.zp4rker.disbot.Main.Companion.jda
import com.zp4rker.disbot.config.TomlFile

/**
 * @author zp4rker
 *
 * Base module class. To be extended by module implementations.
 */
abstract class Module(private val specs: TomlFile) {

    internal var isEnabled = false

    val name = specs.get<String>("name")
    val version = specs.get<String>("version")
    val author = specs.get<String>("author")

    abstract fun onLoad(): Unit

    abstract fun onEnable(): Unit
    abstract fun onDisable(): Unit

    fun getConfig(): TomlFile = TomlFile("$name/config.toml")

    internal fun load() = onLoad()

    internal fun enable() {
        onEnable().runCatching { isEnabled = true }
    }

    internal fun disable() {
        onDisable().runCatching { isEnabled = false }
    }

    fun registerEventHandler(vararg classes: Class<*>) = jda.addEventListener(classes)

}
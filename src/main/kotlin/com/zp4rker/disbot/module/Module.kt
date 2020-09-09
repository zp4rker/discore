package com.zp4rker.disbot.module

import com.zp4rker.disbot.config.TomlFile

/**
 * @author zp4rker
 *
 * Base module class. To be extended by module implementations.
 */
abstract class Module(private val specs: TomlFile) {

    abstract fun onLoad(): Unit

    abstract fun onEnable(): Unit
    abstract fun onDisable(): Unit

    fun getConfig(): TomlFile = TomlFile("${specs["name"]}/config.toml")

}
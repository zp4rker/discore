package com.zp4rker.discore.api

import com.zp4rker.discore.config.JsonFile

class Configuration {

    private val data = JsonFile.loadOrDefault("config.json", true)

    val name: String
        get() = data.optString("bot-name", "notset")

    val token: String
        get() = data.optString("token", "notset")

    val prefix: String
        get() = data.optString("prefix", "notset")

    fun <T> getVal(key: String) = data.get(key) as T

}
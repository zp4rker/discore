package com.zp4rker.core.discord.config

import org.json.JSONObject
import java.io.File

class Config(content: String, private val file: File): JSONObject(content) {

    fun save() {
        file.writeText(this.toString(2))
    }

}
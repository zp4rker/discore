package com.zp4rker.core.discord.util

import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.requests.restaction.MessageAction

fun MessageChannel.sendRawString(string: String): MessageAction {
    var content = string
    val regex = Regex(":[^:]*:")
    regex.findAll(string).forEach {
        val name = it.value.replace(":", "")
        jda.getEmotesByName(name, false).first()?.let { e -> content = content.replace(it.value, e.asMention) }
    }
    return this.sendMessage(content)
}
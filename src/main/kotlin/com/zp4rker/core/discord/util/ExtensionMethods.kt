package com.zp4rker.core.discord.util

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Emote
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel

fun MessageChannel.sendRawString(string: String) = sendMessage(convertEmotes(string, jda))

fun Message.addRawReaction(string: String) = convertEmote(string, jda)?.let { addReaction(it) }!!

private fun convertEmotes(string: String, jda: JDA): String {
    var content = string
    val regex = Regex(":[^:]*:")
    regex.findAll(string).forEach {
        convertEmote(it.value, jda)?.let { e -> content = content.replace(it.value, e.asMention) }
    }
    return content
}

private fun convertEmote(string: String, jda: JDA): Emote? {
    return jda.getEmotesByName(string.replace(":", ""), false).first()
}
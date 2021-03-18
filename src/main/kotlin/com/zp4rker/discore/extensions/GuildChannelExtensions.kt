package com.zp4rker.discore.extensions

import net.dv8tion.jda.api.entities.Category
import net.dv8tion.jda.api.entities.GuildChannel
import net.dv8tion.jda.api.entities.TextChannel

/**
 * @author zp4rker
 */
// General
fun GuildChannel.setName(name: String) = manager.setName(name)
fun GuildChannel.setParent(parent: Category?) = manager.setParent(parent)
fun GuildChannel.setPosition(position: Int) = manager.setPosition(position)

// Text Channels
fun TextChannel.setTopic(topic: String?) = manager.setTopic(topic)
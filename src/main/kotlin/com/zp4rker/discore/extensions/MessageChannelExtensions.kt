package com.zp4rker.discore.extensions

import com.zp4rker.discore.Predicate
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.MessageReaction
import net.dv8tion.jda.api.entities.SelfUser
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent

/**
 * @author zp4rker
 */

@Deprecated("experimental")
fun MessageChannel.awaitMessages(filter: Predicate<Message> = { true }, limit: Int = 1): List<Message> {
    val list = mutableListOf<Message>()
    while (list.size < limit) {
        this.expectBlocking<MessageReceivedEvent> {
            if (filter(it.message) && it.author !is SelfUser) list.add(it.message)
        }
    }
    return list
}

@Deprecated("experimental")
fun MessageChannel.awaitReactions(filter: Predicate<MessageReaction> = { true }, limit: Int = 1): List<MessageReaction> {
    val list = mutableListOf<MessageReaction>()
    while (list.size < limit) {
        this.expectBlocking<MessageReactionAddEvent> {
            if (filter(it.reaction) && it.user !is SelfUser) list.add(it.reaction)
        }
    }
    return list
}
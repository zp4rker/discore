package com.zp4rker.discore.extenstions

import com.zp4rker.discore.extenstions.event.Predicate
import com.zp4rker.discore.extenstions.event.expectBlocking
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

/**
 * @author zp4rker
 */

fun MessageChannel.awaitMessages(filter: Predicate<Message> = { true }, limit: Int = 1): List<Message> {
    val list = mutableListOf<Message>()
    while (list.size < limit) {
        this.expectBlocking<MessageReceivedEvent>(amount = limit) {
            if (filter(it.message)) list.add(it.message)
        }
    }
    return list
}
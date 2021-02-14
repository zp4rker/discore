package com.zp4rker.discore.extenstions

import com.zp4rker.discore.extenstions.event.expectBlocking
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.util.concurrent.TimeUnit

/**
 * @author zp4rker
 */

fun MessageChannel.awaitMessages(limit: Int = 1, timeout: Long = 0, timeoutUnit: TimeUnit = TimeUnit.MILLISECONDS): List<Message> {
    val list = mutableListOf<Message>()
    this.expectBlocking<MessageReceivedEvent>(amount = limit, timeout = timeout, timeoutUnit = timeoutUnit) {
        list.add(it.message)
    }
    return list
}
package com.zp4rker.discore.extensions

import com.zp4rker.discore.Predicate
import com.zp4rker.discore.event.unregister
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.MessageReaction
import net.dv8tion.jda.api.entities.SelfUser
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import java.util.concurrent.CompletableFuture

/**
 * @author zp4rker
 */
fun MessageChannel.collectMessages(filter: Predicate<Message> = { true }, limit: Int): CompletableFuture<List<Message>> {
    val future = CompletableFuture<List<Message>>()
    val list = mutableListOf<Message>()
    on<MessageReceivedEvent> {
        if (filter(it.message) && it.author !is SelfUser) {
            list.add(it.message)
        }

        if (list.size == limit) {
            future.complete(list)
            unregister()
        }
    }
    return future
}

fun MessageChannel.nextMessage(filter: Predicate<Message> = { true }): CompletableFuture<Message> {
    val future = CompletableFuture<Message>()
    on<MessageReceivedEvent> {
        if (filter(it.message) && it.author !is SelfUser) {
            future.complete(it.message)
            unregister()
        }
    }
    return future
}

fun MessageChannel.collectReactions(filter: Predicate<MessageReactionAddEvent> = { true }, limit: Int): CompletableFuture<List<MessageReaction>> {
    val future = CompletableFuture<List<MessageReaction>>()
    val list = mutableListOf<MessageReaction>()
    on<MessageReactionAddEvent> {
        if (filter(it) && it.user !is SelfUser) {
            list.add(it.reaction)
        }

        if (list.size == limit) {
            future.complete(list)
            unregister()
        }
    }
    return future
}

fun MessageChannel.nextReaction(filter: Predicate<MessageReactionAddEvent> = { true }): CompletableFuture<MessageReaction> {
    val future = CompletableFuture<MessageReaction>()
    on<MessageReactionAddEvent> {
        if (filter(it) && it.user !is SelfUser) {
            future.complete(it.reaction)
        }
    }
    return future
}
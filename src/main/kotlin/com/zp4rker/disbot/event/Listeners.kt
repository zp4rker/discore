package com.zp4rker.disbot.event

import com.zp4rker.disbot.Bot
import com.zp4rker.disbot.console.Console
import net.dv8tion.jda.api.events.Event
import net.dv8tion.jda.api.hooks.SubscribeEvent

/**
 * @author zp4rker
 */
interface ExtendedListener<in T: Event> {
//    @SubscribeEvent
    fun onEvent(event: T)
}

inline fun <reified T: Event> listener(crossinline action: Any.(T) -> Unit) = object : ExtendedListener<T> {
    @SubscribeEvent
    override fun onEvent(event: T) {
        action(event)
    }
}

inline fun <reified T: Event> Bot.on(crossinline action: Any.(T) -> Unit) {
    listener(action).also {
        println("attempted register")
        Console.jda?.addEventListener(it)
        println("completed attempt")
    }
}
package com.zp4rker.disbot.extenstions

import com.zp4rker.disbot.Bot
import com.zp4rker.disbot.console.Console
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.EventListener

/**
 * @author zp4rker
 */
interface ExtendedListener<in T : GenericEvent> : EventListener {
    override fun onEvent(event: GenericEvent)
}

inline fun <reified T : GenericEvent> listener(crossinline action: T.() -> Unit) = object : ExtendedListener<T> {
    override fun onEvent(event: GenericEvent) {
        if (event is T) action(event)
    }
}

inline fun <reified T : GenericEvent> Bot.on(crossinline action: T.() -> Unit) {
    listener(action).also {
        Console.jda?.addEventListener(it)
    }
}

inline fun <reified T: GenericEvent> JDA.on(crossinline action: T.() -> Unit) {
    listener(action).also {
        addEventListener(it)
    }
}
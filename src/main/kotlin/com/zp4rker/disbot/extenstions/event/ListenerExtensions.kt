package com.zp4rker.disbot.extenstions.event

import com.zp4rker.disbot.API
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.EventListener
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * @author zp4rker
 */
interface ExtendedListener<in T : GenericEvent> : EventListener {
    override fun onEvent(event: GenericEvent)
}

inline fun <reified T : GenericEvent> listener(crossinline action: EventListener.(T) -> Unit) = object : ExtendedListener<T> {
    override fun onEvent(event: GenericEvent) {
        if (event is T) action(event)
    }
}

fun EventListener.unregister() {
    API.removeEventListener(this)
}

inline fun <reified T : GenericEvent> JDA.on(crossinline action: EventListener.(T) -> Unit) = listener(action).also {
    addEventListener(it)
}

val expectationPool: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

inline fun <reified T : GenericEvent> JDA.expect(
        amount: Int = 1,
        crossinline predicate: (T) -> Boolean = { true },
        timeout: Long = 0,
        timeoutUnit: TimeUnit = TimeUnit.MILLISECONDS,
        crossinline timeoutAction: () -> Unit = {},
        crossinline action: (T) -> Unit
): ExtendedListener<T> {
    var callCount = 0
    val listener = on<T> {
        if (predicate(it)) {
            action(it)
            if (++callCount >= amount) {
                unregister()
            }
        }
    }

    if (timeout > 0) {
        expectationPool.schedule({
            if (callCount < amount) {
                timeoutAction()
                listener.unregister()
            }
        }, timeout, timeoutUnit)
    }

    return listener
}

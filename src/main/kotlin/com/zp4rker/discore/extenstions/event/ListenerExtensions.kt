package com.zp4rker.discore.extenstions.event

import com.zp4rker.discore.API
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.EventListener
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * @author zp4rker
 */
interface ExtendedListener<in T : GenericEvent> : EventListener {
    override fun onEvent(event: GenericEvent)
}

typealias Predicate<T> = (T) -> Boolean

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

inline fun <reified T : GenericEvent> JDA.on(crossinline predicate: Predicate<T>, crossinline action: EventListener.(T) -> Unit) = on<T> {
    if (predicate(it)) action(it)
}

val expectationPool: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

inline fun <reified T : GenericEvent> JDA.expect(
    crossinline predicate: Predicate<T> = { true },
    amount: Int = 1,
    timeout: Long = 0,
    timeoutUnit: TimeUnit = TimeUnit.MILLISECONDS,
    crossinline timeoutAction: () -> Unit = {},
    crossinline action: EventListener.(T) -> Unit
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

inline fun <reified T : GenericEvent> JDA.expectBlocking(
    crossinline predicate: Predicate<T> = {true},
    amount: Int = 1,
    timeout: Long = 0,
    timeoutUnit: TimeUnit = TimeUnit.MILLISECONDS,
    crossinline timeoutAction: () -> Unit = {},
    crossinline action: EventListener.(T) -> Unit) {
    val lock = ReentrantLock()
    val cond = lock.newCondition()
    var flag = false
    val unlock: () -> Unit = {
        flag = true
        lock.withLock {
            cond.signal()
        }
    }
    expect(predicate, amount, timeout, timeoutUnit, { unlock(); timeoutAction() }) {
        action(it)
        unlock()
    }
    lock.withLock {
        while (!flag) cond.await()
    }
}

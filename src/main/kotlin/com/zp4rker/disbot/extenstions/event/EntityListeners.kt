package com.zp4rker.disbot.extenstions.event

import com.zp4rker.disbot.API
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.channel.text.GenericTextChannelEvent
import net.dv8tion.jda.api.events.guild.GenericGuildEvent
import net.dv8tion.jda.api.events.guild.member.GenericGuildMemberEvent
import net.dv8tion.jda.api.events.message.GenericMessageEvent
import net.dv8tion.jda.api.events.user.GenericUserEvent
import net.dv8tion.jda.api.hooks.EventListener
import java.util.concurrent.TimeUnit

/**
 * @author zp4rker
 */

/* User events */
inline fun <reified T: GenericUserEvent> User.on(crossinline action: EventListener.(T) -> Unit) = API.on<T> {
    if (it.user == this@on) action(it)
}

inline fun <reified T: GenericUserEvent> User.expect(
        amount: Int = 1,
        crossinline predicate: (T) -> Boolean = { true },
        timeout: Long = 0,
        timeoutUnit: TimeUnit = TimeUnit.MILLISECONDS,
        crossinline timeoutAction: () -> Unit = {},
        crossinline action: (T) -> Unit
) = API.expect(amount, predicate, timeout, timeoutUnit, timeoutAction) {
    if (it.user == this@expect) action(it)
}

/* Member events */
inline fun <reified T: GenericGuildMemberEvent> Member.on(crossinline action: EventListener.(T) -> Unit) = API.on<T> {
    if (it.member == this@on) action(it)
}

inline fun <reified T: GenericGuildMemberEvent> Member.expect(
        amount: Int = 1,
        crossinline predicate: (T) -> Boolean = { true },
        timeout: Long = 0,
        timeoutUnit: TimeUnit = TimeUnit.MILLISECONDS,
        crossinline timeoutAction: () -> Unit = {},
        crossinline action: (T) -> Unit
) = API.expect(amount, predicate, timeout, timeoutUnit, timeoutAction) {
    if (it.member == this@expect) action(it)
}

/* Message events */
inline fun <reified T: GenericMessageEvent> Message.on(crossinline action: EventListener.(T) -> Unit) = API.on<T> {
    if (it.messageId == this@on.id) action(it)
}

inline fun <reified T: GenericMessageEvent> Message.expect(
        amount: Int = 1,
        crossinline predicate: (T) -> Boolean = { true },
        timeout: Long = 0,
        timeoutUnit: TimeUnit = TimeUnit.MILLISECONDS,
        crossinline timeoutAction: () -> Unit = {},
        crossinline action: (T) -> Unit
) = API.expect(amount, predicate, timeout, timeoutUnit, timeoutAction) {
    if (it.messageId == this@expect.id) action(it)
}

/* Text Channel events */
inline fun <reified T: GenericTextChannelEvent> TextChannel.on(crossinline action: EventListener.(T) -> Unit) = API.on<T> {
    if (it.channel == this@on) action(it)
}

inline fun <reified T: GenericTextChannelEvent> TextChannel.expect(
        amount: Int = 1,
        crossinline predicate: (T) -> Boolean = { true },
        timeout: Long = 0,
        timeoutUnit: TimeUnit = TimeUnit.MILLISECONDS,
        crossinline timeoutAction: () -> Unit = {},
        crossinline action: (T) -> Unit
) = API.expect(amount, predicate, timeout, timeoutUnit, timeoutAction) {
    if (it.channel == this@expect) action(it)
}

/* Guild events */
inline fun <reified T: GenericGuildEvent> Guild.on(crossinline action: EventListener.(T) -> Unit) = API.on<T> {
    if (it.guild == this@on) action(it)
}

inline fun <reified T: GenericGuildEvent> Guild.expect(
        amount: Int = 1,
        crossinline predicate: (T) -> Boolean = { true },
        timeout: Long = 0,
        timeoutUnit: TimeUnit = TimeUnit.MILLISECONDS,
        crossinline timeoutAction: () -> Unit = {},
        crossinline action: (T) -> Unit
) = API.expect(amount, predicate, timeout, timeoutUnit, timeoutAction) {
    if (it.guild == this@expect) action(it)
}
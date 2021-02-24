package com.zp4rker.discore.extenstions.event

import com.zp4rker.discore.API
import com.zp4rker.discore.LOGGER
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.channel.text.GenericTextChannelEvent
import net.dv8tion.jda.api.events.guild.GenericGuildEvent
import net.dv8tion.jda.api.events.guild.member.GenericGuildMemberEvent
import net.dv8tion.jda.api.events.message.GenericMessageEvent
import net.dv8tion.jda.api.events.message.guild.GenericGuildMessageEvent
import net.dv8tion.jda.api.events.message.priv.GenericPrivateMessageEvent
import net.dv8tion.jda.api.events.user.GenericUserEvent
import net.dv8tion.jda.api.hooks.EventListener
import java.util.concurrent.TimeUnit

/**
 * @author zp4rker
 */

inline fun <reified T : GenericEvent> ISnowflake.on(noinline action: EventListener.(T) -> Unit) = API.on<T> {
    runAction(this@on, it, this, action)
}

inline fun <reified T : GenericEvent> ISnowflake.on(crossinline predicate: Predicate<T> = { true }, noinline action: EventListener.(T) -> Unit) = API.on(predicate) {
    runAction(this@on, it, this, action)
}

inline fun <reified T : GenericEvent> ISnowflake.expect(
    crossinline predicate: Predicate<T> = { true },
    amount: Int = 1,
    timeout: Long = 0,
    timeoutUnit: TimeUnit = TimeUnit.MILLISECONDS,
    crossinline timeoutAction: () -> Unit = {},
    noinline action: EventListener.(T) -> Unit
) = API.expect(predicate, amount, timeout, timeoutUnit, timeoutAction) {
    runAction(this@expect, it, this, action)
}

inline fun <reified T : GenericEvent> ISnowflake.expectBlocking(
    crossinline predicate: Predicate<T> = { true },
    amount: Int = 1,
    timeout: Long = 0,
    timeoutUnit: TimeUnit = TimeUnit.MILLISECONDS,
    crossinline timeoutAction: () -> Unit = {},
    noinline action: EventListener.(T) -> Unit
) = API.expectBlocking(predicate, amount, timeout, timeoutUnit, timeoutAction) {
    runAction(this@expectBlocking, it, this, action)
}

inline fun <reified T : GenericEvent> runAction(
    entity: ISnowflake,
    event: T,
    eventListener: EventListener,
    noinline action: EventListener.(T) -> Unit
) {
    when (entity) {
        is User -> {
            if (event is GenericUserEvent) {
                if (event.user == entity) action(eventListener, event)
            } else {
                LOGGER.warn("Unrecognised event for entity type!")
                eventListener.unregister()
            }
        }

        is Member -> {
            if (event is GenericGuildMemberEvent) {
                if (event.member == entity) action(eventListener, event)
            } else {
                LOGGER.warn("Unrecognised event for entity type!")
                eventListener.unregister()
            }
        }

        is Message -> {
            if (event is GenericMessageEvent) {
                if (event.messageId == entity.id) action(eventListener, event)
            } else {
                LOGGER.warn("Unrecognised event for entity type!")
                eventListener.unregister()
            }
        }

        is MessageChannel -> {
            if (event is GenericTextChannelEvent) {
                if (event.channel == entity) action(eventListener, event)
            } else if (event is GenericGuildMessageEvent) {
                if (event.channel == entity) action(eventListener, event)
            } else if (event is GenericMessageEvent) {
                if (event.channel == entity) action(eventListener, event)
            } else {
                LOGGER.warn("Unrecognised event for entity type!")
                eventListener.unregister()
            }
        }

        is PrivateChannel -> {
            if (event is GenericPrivateMessageEvent) {
                if (event.channel == entity) action(eventListener, event)
            }
        }

        is Guild -> {
            if (event is GenericGuildEvent) {
                if (event.guild == entity) action(eventListener, event)
            } else {
                LOGGER.warn("Unrecognised event for entity type!")
                eventListener.unregister()
            }
        }
    }
}
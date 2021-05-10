package com.zp4rker.discore

import net.dv8tion.jda.api.JDA
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author zp4rker
 */
const val DISCORE_VERSION = "@VERSION@"

lateinit var API: JDA
lateinit var BOT: Bot

val LOGGER: Logger = LoggerFactory.getLogger("discore")

typealias Predicate<T> = (T) -> Boolean
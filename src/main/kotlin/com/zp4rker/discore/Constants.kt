package com.zp4rker.discore

import net.dv8tion.jda.api.JDA
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.JarURLConnection
import java.net.URL
import java.util.jar.Attributes

/**
 * @author zp4rker
 */

lateinit var API: JDA
lateinit var BOT: Bot

var DEBUG: Boolean = false

val LOGGER: Logger = LoggerFactory.getLogger("discore")

const val HIDDEN_EMBED_COLOUR = "#2f3136"

val MANIFEST: Attributes = getManifest(Bot::class.java.protectionDomain.codeSource.location)

fun getManifest(url: URL): Attributes = with(URL("jar:${url.toExternalForm()}!/").openConnection() as JarURLConnection) { mainAttributes }

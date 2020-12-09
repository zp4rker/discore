package com.zp4rker.dsc.disbot

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

val MANIFEST: Attributes = getManifest(Bot::class.java.protectionDomain.codeSource.location)

val LOGGER: Logger = LoggerFactory.getLogger("Disbot")

const val HIDDEN_EMBED_COLOUR = "#2f3136"

fun getManifest(url: URL): Attributes = with(URL("jar:${url.toExternalForm()}!/").openConnection() as JarURLConnection) { mainAttributes }

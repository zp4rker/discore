package com.zp4rker.disbot

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

val MANIFEST: Attributes = run {
    val url = Bot::class.java.protectionDomain.codeSource.location.let { URL("jar:${it.toExternalForm()}!/") }
    val juc = url.openConnection() as JarURLConnection
    juc.mainAttributes
}
val LOGGER: Logger = LoggerFactory.getLogger("Disbot")

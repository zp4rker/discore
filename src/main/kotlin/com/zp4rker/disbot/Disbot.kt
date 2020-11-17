package com.zp4rker.disbot

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.JarURLConnection
import java.net.URL

/**
 * @author zp4rker
 */

val disbotVersion: String = Bot::class.java.`package`.implementationVersion
val jdaVersion: String = run {
    val url = Bot::class.java.protectionDomain.codeSource.location.let { URL("jar:${it.toExternalForm()}!/") }
    val juc = url.openConnection() as JarURLConnection
    val mf = juc.mainAttributes
    mf.getValue("JDA-Version")
}
val globalLogger: Logger = LoggerFactory.getLogger("Disbot")
package com.zp4rker.discore.console

import ch.qos.logback.classic.Level
import com.zp4rker.discore.BOT
import org.fusesource.jansi.Ansi
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * @author zp4rker
 */

fun log(loggerName: String, lvl: Level, output: Any? = "", error: Throwable? = null, debug: Boolean = false) {
    if (output?.toString()?.contains("\n") == true) {
        output.toString().split("\n").forEach { log(loggerName, lvl, it, error, debug) }
        return
    }

    if ((debug && BOT.debug) || !debug) {
        output?.let {
            when (lvl) {
                Level.INFO -> LoggerFactory.getLogger("discore:$loggerName").info(it.toString())
                Level.DEBUG -> LoggerFactory.getLogger("discore:$loggerName").debug(it.toString())

                Level.WARN -> LoggerFactory.getLogger("discore:$loggerName").warn(it.toString())
                Level.ERROR -> error?.let { e ->
                    LoggerFactory.getLogger("discore:$loggerName").error(it.toString(), e)
                } ?: LoggerFactory.getLogger("discore:$loggerName").error(it.toString())
            }
        }
    }

    val logFile = File("logs/log.txt").also {
        if (!it.parentFile.exists()) it.parentFile.mkdirs()
        if (!it.exists()) it.createNewFile()
    }

    val logOut = output?.toString()?.replace(Regex("\u001B\\[[;\\d]*m"), "") ?: "null"
    val timestamp = if (logOut != "") {
        "[${OffsetDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))}] \t"
    } else {
        ""
    }

    logFile.appendText("$timestamp$logOut\n")
}

fun Logger.separator() {
    info(Ansi.ansi().fgBrightBlack().a("=".repeat(50)).reset())
}

fun Logger.blankLine() {
    info("")
}

fun Logger.info(ansi: Ansi) = info(ansi.toString())
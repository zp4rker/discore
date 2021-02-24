package com.zp4rker.discore.log

import com.zp4rker.discore.LOGGER
import com.zp4rker.discore.util.datedArchive
import com.zp4rker.log4kt.Log4KtEventListener
import com.zp4rker.log4kt.Log4KtLogEvent
import com.zp4rker.log4kt.Log4KtPrepareLogEvent
import org.fusesource.jansi.Ansi
import org.slf4j.Logger
import org.slf4j.event.Level
import java.io.File
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * @author zp4rker
 */

fun initLogBackend() {
    datedArchive(File("logs/log.txt"))

    Log4KtEventListener.on<Log4KtPrepareLogEvent> {
        if (it.level != Level.INFO || !(it.msg?.contains("\n") ?: false)) return@on

        it.isCancelled = true

        it.msg?.run { split("\n").forEach { line -> LOGGER.info(line) } }
    }

    Log4KtEventListener.on<Log4KtLogEvent> {
        val logFile = File("logs/log.txt").also {
            if (!it.parentFile.exists()) it.parentFile.mkdirs()
            if (!it.exists()) it.createNewFile()
        }

        val logOut = it.output?.replace(Regex("\u001B\\[[;\\d]*m"), "") ?: "null"
        val timestamp = if (logOut != "") {
            "[${OffsetDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))}] \t"
        } else {
            ""
        }

        logFile.appendText("$timestamp$logOut\n")
    }
}

fun Logger.blankLine() {
    info("")
}

fun Logger.info(ansi: Ansi) = info(ansi.toString())
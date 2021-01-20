package com.zp4rker.discore.console

import com.zp4rker.discore.bootstrap.Main
import org.fusesource.jansi.Ansi
import org.slf4j.Logger
import java.io.File
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * @author zp4rker
 */

fun log(output: Any? = "", debug: Boolean = false) {
    if (output?.toString()?.contains("\n") == true) {
        output.toString().split("\n").forEach(::log)
        return
    }

    if ((debug && Main.debug) || !debug) println(output)

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

fun separator() {
    log(Ansi.ansi().fgBrightBlack().a("=".repeat(50)).reset())
}

fun Logger.info(ansi: Ansi) = info(ansi.toString())
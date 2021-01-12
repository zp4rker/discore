package com.zp4rker.discore.extenstions

import org.fusesource.jansi.Ansi
import org.fusesource.jansi.Ansi.ansi
import org.slf4j.Logger

/**
 * @author zp4rker
 */

fun Logger.separator() {
    info(ansi().fgBrightBlack().a("=".repeat(40)).reset())
}

fun Logger.info(ansi: Ansi) {
    info(ansi.toString())
}

fun Logger.s(msg: Any?) {
    info(ansi().fgBrightGreen().bold().a("${(msg ?: "null")}").reset())
}

fun Logger.w(msg: Any?) {
    info(ansi().fgYellow().bold().a("${msg ?: "null"}").reset())
}

fun Logger.e(msg: Any?) {
    info(ansi().fgBrightRed().bold().a("${msg ?: "null"}").reset())
}
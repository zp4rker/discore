package com.zp4rker.discore.log

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.turbo.TurboFilter
import ch.qos.logback.core.spi.FilterReply
import org.fusesource.jansi.Ansi
import org.slf4j.Marker

/**
 * @author zp4rker
 *
 * Relog class name logger logs.
 */
class LogInterceptor : TurboFilter() {

    override fun decide(marker: Marker?, logger: Logger?, level: Level?, format: String?, params: Array<Any>?, t: Throwable?): FilterReply {
        logger ?: return FilterReply.DENY

        if (logger.name.startsWith("discore:")) return FilterReply.ACCEPT

        if (level == Level.TRACE) return FilterReply.DENY

        val lvl = level ?: Level.INFO

        val message = format?.run {
            if (params == null) {
                this
            } else {
                this.let {
                    var s = it
                    for (param in params) {
                        s = s.replaceFirst("{}", param.toString())
                    }
                    s
                }
            }
        } ?: "No message"

        with(Ansi.ansi()) {
            reset()
            a(message)

            log(logger.name, lvl, this, t, lvl == Level.DEBUG)

            if (message == "Finished Loading!") log(logger.name, lvl, "")
        }

        return FilterReply.DENY
    }

}
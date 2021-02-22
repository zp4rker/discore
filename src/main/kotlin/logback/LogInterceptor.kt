package logback

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.turbo.TurboFilter
import ch.qos.logback.core.spi.FilterReply
import com.zp4rker.discore.console.log
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

        if (level == Level.TRACE) return FilterReply.DENY

        if (logger.name.startsWith("discore:")) return FilterReply.ACCEPT

        val nameRaw = logger.name.run { split(".").getOrElse(2) { this } }.run {
            when {
                length > 7 -> "${this.substring(0..4)}.."
                else -> this
            }
        }
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

            fgBrightBlack()
            a(nameRaw.padEnd(8))

            when (lvl) {
                Level.INFO -> fgBrightGreen()
                Level.WARN -> fgBrightYellow()
                Level.ERROR -> fgBrightRed()
                else -> fgBlack()
            }
            a(lvl.levelStr.padEnd(8))

            reset()
            a(message)

            if (t != null) {
                a("\n${t.stackTraceToString()}")
            }

            log(logger.name, level ?: Level.INFO, this, t, lvl == Level.DEBUG)

            if (message == "Finished Loading!") log(logger.name, level ?: Level.INFO, "")
        }

        return FilterReply.DENY
    }

}
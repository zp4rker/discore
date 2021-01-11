package logback

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.turbo.TurboFilter
import ch.qos.logback.core.spi.FilterReply
import org.fusesource.jansi.Ansi.ansi
import org.slf4j.LoggerFactory
import org.slf4j.Marker

/**
 * @author zp4rker
 *
 * Relog class name logger logs.
 */
class LogInterceptor : TurboFilter() {

    override fun decide(marker: Marker?, logger: Logger, level: Level, msg: String?, objects: Array<Any>?, t: Throwable?): FilterReply {
        val message = msg ?: "null"

        if (!logger.name.contains(".")) return FilterReply.ACCEPT

        val newLogger = if (logger.name.startsWith("net.dv8tion.jda")) {
            LoggerFactory.getLogger("JDA")
        } else LoggerFactory.getLogger(logger.name.substringAfterLast(".").toUpperCase())

        when (level) {
            Level.INFO -> objects?.let { newLogger.info(message, it) } ?: t?.let { newLogger.info(message, it) } ?: newLogger.info(message)
            //Level.DEBUG -> objects?.let { newLogger.debug(msg, it) } ?: t?.let { newLogger.debug(msg, it) } ?: newLogger.debug(msg)
            Level.ERROR -> objects?.let { newLogger.error(message, it) } ?: t?.let { newLogger.error(message, it) } ?: newLogger.error(message)
            Level.WARN -> objects?.let { newLogger.warn(message, it) } ?: t?.let { newLogger.warn(message, it) } ?: newLogger.warn(message)
            //Level.TRACE -> objects?.let { newLogger.trace(msg, it) } ?: t?.let { newLogger.trace(msg, it) } ?: newLogger.trace(msg)
        }
        if (level != Level.TRACE && level != Level.DEBUG) return FilterReply.DENY

        return FilterReply.ACCEPT
    }

}
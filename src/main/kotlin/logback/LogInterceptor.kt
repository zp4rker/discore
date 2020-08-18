package logback

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.turbo.TurboFilter
import ch.qos.logback.core.spi.FilterReply
import org.slf4j.LoggerFactory
import org.slf4j.Marker

class LogInterceptor : TurboFilter() {

    override fun decide(marker: Marker?, logger: Logger, level: Level, msg: String, objects: Array<Any>?, t: Throwable?): FilterReply {
        if (logger.name.startsWith("net.dv8tion.jda")) {
            val newLogger = LoggerFactory.getLogger("JDA")
            when (level) {
                Level.INFO -> objects?.let { newLogger.info(msg, it) } ?: t?.let { newLogger.info(msg, it) } ?: newLogger.info(msg)
                //Level.DEBUG -> objects?.let { newLogger.debug(msg, it) } ?: t?.let { newLogger.debug(msg, it) } ?: newLogger.debug(msg)
                Level.ERROR -> objects?.let { newLogger.error(msg, it) } ?: t?.let { newLogger.error(msg, it) } ?: newLogger.error(msg)
                Level.WARN -> objects?.let { newLogger.warn(msg, it) } ?: t?.let { newLogger.warn(msg, it) } ?: newLogger.warn(msg)
                //Level.TRACE -> objects?.let { newLogger.trace(msg, it) } ?: t?.let { newLogger.trace(msg, it) } ?: newLogger.trace(msg)
            }
            return FilterReply.DENY
        }
        return FilterReply.ACCEPT
    }

}
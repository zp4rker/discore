package logback

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.filter.Filter
import ch.qos.logback.core.spi.FilterReply

/**
 * @author zp4rker
 *
 * Filters out class name loggers and debug and trace levels.
 */
class DenyLog : Filter<ILoggingEvent>() {

    override fun decide(event: ILoggingEvent): FilterReply {
        if (event.loggerName.contains(".")) return FilterReply.DENY
        if (event.level == Level.DEBUG || event.level == Level.TRACE) return FilterReply.DENY

        return FilterReply.ACCEPT
    }

}
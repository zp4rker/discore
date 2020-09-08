package logback

import ch.qos.logback.classic.pattern.MessageConverter
import ch.qos.logback.classic.spi.ILoggingEvent

/**
 * @author zp4rker
 *
 * Appends logger name for other than main loggers.
 */
class LogConverter : MessageConverter() {

    override fun convert(event: ILoggingEvent): String {
        return if (event.loggerName == "Disbot") {
            super.convert(event)
        } else {
            "[${event.loggerName}] ${super.convert(event)}"
        }
    }

}
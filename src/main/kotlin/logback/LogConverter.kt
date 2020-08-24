package logback

import ch.qos.logback.classic.pattern.MessageConverter
import ch.qos.logback.classic.spi.ILoggingEvent

class LogConverter : MessageConverter() {

    override fun convert(event: ILoggingEvent): String {
        return if (event.loggerName == "Disbot") {
            super.convert(event)
        } else {
            "[${event.loggerName}] ${super.convert(event)}"
        }
    }

}
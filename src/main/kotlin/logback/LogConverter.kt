package logback

import ch.qos.logback.classic.pattern.MessageConverter
import ch.qos.logback.classic.spi.ILoggingEvent

class LogConverter : MessageConverter() {

    override fun convert(event: ILoggingEvent): String {
        return if (event.loggerName == "Discore") {
            "[${event.level.levelStr}]\t\t${super.convert(event)}"
        } else {
            "[${event.loggerName}/${event.level.levelStr}]\t${super.convert(event)}"
        }
    }

}
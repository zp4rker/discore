package logback

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.filter.Filter
import ch.qos.logback.core.spi.FilterReply

class DenyLog : Filter<ILoggingEvent>() {

    override fun decide(event: ILoggingEvent): FilterReply {
        return if (!event.loggerName.startsWith("net.dv8tion.jda")) FilterReply.ACCEPT
        else FilterReply.DENY
    }

}
package logback

import ch.qos.logback.core.joran.spi.NoAutoStart
import ch.qos.logback.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy

/**
 * @author zp4rker
 */
@NoAutoStart
class StartupBasedTriggeringPolicy<E> : DefaultTimeBasedFileNamingAndTriggeringPolicy<E>() {
    override fun start() {
        super.start()
        nextCheck = 0L
        isTriggeringEvent(null, null)
        runCatching { tbrp.rollover() }
    }
}
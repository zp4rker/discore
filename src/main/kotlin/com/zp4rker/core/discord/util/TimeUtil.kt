package com.zp4rker.core.discord.util

import java.time.Instant
import java.util.concurrent.TimeUnit

object TimeUtil {
    fun toShortString(instant: Instant): String = toUnits(instant).map { "${it.value}${it.key}" }.toList().joinToString()

    fun toLongString(instant: Instant): String {
        val u = toUnits(instant)
        val days = u["d"]
        val hours = u["h"]
        val minutes = u["m"]
        val seconds = u["s"]

        val d = "$days day${if (days!! > 1) "s" else ""}"
        val h = if (hours!! > 1) "$hours hours" else "$hours hour"
        val m = if (minutes!! > 1) "$minutes minutes" else "$minutes minute"
        val s = if (seconds!! > 1) "$seconds seconds" else "$seconds second"
        val units = arrayOf(d, h, m, s).filter { it.isNotEmpty() }
        return units.dropLast(1).joinToString() + if (units.size < 2) "" else " and " + units.last()
    }

    private fun toUnits(instant: Instant): Map<String, Long> {
        val now = Instant.now()
        var timePast = if (now > instant) now.epochSecond - instant.epochSecond else instant.epochSecond - now.epochSecond
        println(now.toString())
        println(instant.toString())

        val days = TimeUnit.SECONDS.toDays(timePast)
        timePast -= TimeUnit.DAYS.toSeconds(days)

        val hours = TimeUnit.SECONDS.toHours(timePast)
        timePast -= TimeUnit.HOURS.toSeconds(hours)

        val minutes = TimeUnit.SECONDS.toMinutes(timePast)
        timePast -= TimeUnit.MINUTES.toSeconds(minutes)

        val seconds = timePast

        return mapOf("d" to days, "h" to hours, "m" to minutes, "s" to seconds).filter { it.value > 0 }
    }
}
package co.zpdev.core.discord.util;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * Util for converting an instant to a readable string.
 *
 * @author ZP4RKER
 */
public class TimeUtil {

    /**
     * Converts an instant into a readable string.
     *
     * @param instant the instant to convert
     * @param full whether to use full version or not
     * @return the resulting string
     */
    public static String toString(Instant instant, boolean full) {
        Instant now = Instant.now();
        long timePast = now.compareTo(instant) > 0 ? now.getEpochSecond() - instant.getEpochSecond() : instant.getEpochSecond() - now.getEpochSecond();

        long days = TimeUnit.SECONDS.toDays(timePast);
        timePast -= TimeUnit.DAYS.toSeconds(days);

        long hours = TimeUnit.SECONDS.toHours(timePast);
        timePast -= TimeUnit.HOURS.toSeconds(hours);

        long minutes = TimeUnit.SECONDS.toMinutes(timePast);
        timePast -= TimeUnit.MINUTES.toSeconds(minutes);

        long seconds = timePast;

        String d, h, m, s;

        if (!full) {
            d = days > 0 ? days + "d " : "";
            h = hours > 0 ? hours + "h " : "";
            m = minutes > 0 ? minutes + "m " : "";
            s = seconds > 0 ? seconds + "s" : "";
        } else {
            d = days == 0 ? "" : days + (days == 1 ? " day " : " days ");
            h = hours == 0 ? "" : hours + (hours == 1 ? " hour " : " hours ");
            m = minutes == 1 ? " minute " : " minutes ";
            s = seconds == 1 ? " second " : " seconds ";
        }

        return d + h + m + (!full ? "" : "and ") + s;
    }

}

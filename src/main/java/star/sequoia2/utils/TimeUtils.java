package star.sequoia2.utils;

import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class TimeUtils {
    private TimeUtils() {}

    public static Instant parseTimestamp(String timestamp) {
        if (StringUtils.endsWith(timestamp, "000")) {
            return Instant.parse(timestamp.replace("000", "Z"));
        }
        return Instant.parse(timestamp);
    }

    public static String toPrettyTimeSince(String timestamp) {
        long totalSeconds =
                (System.currentTimeMillis() - parseTimestamp(timestamp).toEpochMilli()) / 1000;
        return toPrettyTime(totalSeconds);
    }

    public static String toPrettyTime(long totalSeconds) {
        Duration duration = Duration.ofSeconds(totalSeconds);

        long years = duration.toDays() / 365;
        duration = duration.minusDays(years * 365);

        long months = duration.toDays() / 30;
        duration = duration.minusDays(months * 30);

        long days = duration.toDays();
        duration = duration.minusDays(days);

        long hours = duration.toHours();
        duration = duration.minusHours(hours);

        long minutes = duration.toMinutes();
        duration = duration.minusMinutes(minutes);

        long seconds = duration.getSeconds();

        return (years > 0 ? years + "y " : "") + (months > 0 ? months + "mo " : "")
                + (days > 0 ? days + "d " : "")
                + (hours > 0 ? hours + "h " : "")
                + (minutes > 0 ? minutes + "m " : "")
                + seconds
                + "s";
    }

    public static String wsTimestamp() {
        return Instant.ofEpochMilli(System.currentTimeMillis())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ROOT));
    }


    private static final DateTimeFormatter READABLE =
            DateTimeFormatter.ofPattern("yyyy MMMM d", Locale.ENGLISH);

    private static String toReadable(String isoTimestamp, ZoneId zone) {
        Instant instant = Instant.parse(isoTimestamp.replaceFirst("(Z)?$", "Z")); // ensure trailing Z
        ZonedDateTime zdt = instant.atZone(zone);
        return READABLE.format(zdt);
    }

    public static String toReadable(String isoTimestamp) {
        return toReadable(isoTimestamp, ZoneId.systemDefault());
    }


    public static String since(String isoTimestamp) {
        return since(isoTimestamp, ZoneId.systemDefault());
    }

    private static String since(String isoTimestamp, ZoneId displayZone) {
        Instant instant = Instant.parse(isoTimestamp);
        ZonedDateTime then = instant.atZone(displayZone);
        ZonedDateTime now  = ZonedDateTime.now(displayZone);

        if (then.isAfter(now)) {
            return "just now";
        }

        long years   = ChronoUnit.YEARS  .between(then, now);
        if (years > 0)   return format(years,   "year");

        long months  = ChronoUnit.MONTHS .between(then, now);
        if (months > 0)  return format(months,  "month");

        long days    = ChronoUnit.DAYS   .between(then, now);
        if (days > 0)    return format(days,    "day");

        long hours   = ChronoUnit.HOURS  .between(then, now);
        if (hours > 0)   return format(hours,   "hour");

        long minutes = ChronoUnit.MINUTES.between(then, now);
        if (minutes > 0) return format(minutes, "minute");

        long seconds = ChronoUnit.SECONDS.between(then, now);
        return format(seconds, "second");
    }

    private static String format(long value, String unit) {
        return String.format(Locale.ENGLISH, "%d %s%s ago",
                value, unit, value == 1 ? "" : "s");
    }
}
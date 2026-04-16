package com.bristol.domain.shared.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Central time helpers for the Bristol platform.
 * Local calendar/date-time operations are resolved against Argentina time.
 * Persisted instants should still remain absolute UTC instants.
 */
public final class BristolTime {

    public static final ZoneId ARGENTINA_ZONE = ZoneId.of("America/Argentina/Buenos_Aires");
    public static final DateTimeFormatter FORMATTER_WITH_SECONDS =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS z");
    public static final DateTimeFormatter FORMATTER_WITHOUT_SECONDS =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm z");

    private BristolTime() {
    }

    public static ZonedDateTime zonedNow() {
        return ZonedDateTime.now(ARGENTINA_ZONE);
    }

    public static Instant nowInstant() {
        return zonedNow().toInstant();
    }

    public static LocalDateTime nowDateTime() {
        return zonedNow().toLocalDateTime();
    }

    public static LocalDate nowDate() {
        return zonedNow().toLocalDate();
    }

    public static String timestamp() {
        return zonedNow().format(FORMATTER_WITH_SECONDS);
    }

    public static ZonedDateTime parseTimestamp(String date) {
        try {
            return ZonedDateTime.parse(date, FORMATTER_WITH_SECONDS);
        } catch (DateTimeParseException ex) {
            ZonedDateTime dateTime = ZonedDateTime.parse(date, FORMATTER_WITHOUT_SECONDS);
            return dateTime.withSecond(0).withNano(0);
        }
    }

    public static Instant startOfDay(LocalDate date) {
        return date.atStartOfDay(ARGENTINA_ZONE).toInstant();
    }

    public static Instant endOfDay(LocalDate date) {
        return date.atTime(23, 59, 59).atZone(ARGENTINA_ZONE).toInstant();
    }

    public static LocalDate toLocalDate(Instant instant) {
        return instant.atZone(ARGENTINA_ZONE).toLocalDate();
    }
}

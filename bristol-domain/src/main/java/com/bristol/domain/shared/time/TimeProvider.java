package com.bristol.domain.shared.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Time provider abstraction for testability and consistency.
 * All domain logic requiring current time should use this interface.
 */
public interface TimeProvider {

    /**
     * Get current instant in UTC
     */
    Instant now();

    /**
     * Get current local date-time
     */
    LocalDateTime nowDateTime();

    /**
     * Get current local date
     */
    LocalDate nowDate();

    default ZoneId zoneId() {
        return BristolTime.ARGENTINA_ZONE;
    }

    default ZonedDateTime zonedNow() {
        return now().atZone(zoneId());
    }

    default String timestamp() {
        return zonedNow().format(BristolTime.FORMATTER_WITH_SECONDS);
    }

    default ZonedDateTime parseTimestamp(String date) {
        return BristolTime.parseTimestamp(date);
    }

    default Instant startOfDay(LocalDate date) {
        return date.atStartOfDay(zoneId()).toInstant();
    }

    default Instant endOfDay(LocalDate date) {
        return date.atTime(23, 59, 59).atZone(zoneId()).toInstant();
    }

    default LocalDate toLocalDate(Instant instant) {
        return instant.atZone(zoneId()).toLocalDate();
    }
}

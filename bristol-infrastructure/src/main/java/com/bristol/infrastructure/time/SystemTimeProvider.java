package com.bristol.infrastructure.time;

import com.bristol.domain.shared.time.TimeProvider;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * System implementation of TimeProvider using actual system time.
 */
@Component
public class SystemTimeProvider implements TimeProvider {

    @Override
    public Instant now() {
        return java.time.ZonedDateTime.now(zoneId()).toInstant();
    }

    @Override
    public LocalDateTime nowDateTime() {
        return LocalDateTime.now(zoneId());
    }

    @Override
    public LocalDate nowDate() {
        return LocalDate.now(zoneId());
    }
}

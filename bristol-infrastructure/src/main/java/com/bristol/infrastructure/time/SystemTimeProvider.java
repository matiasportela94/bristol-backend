package com.bristol.infrastructure.time;

import com.bristol.domain.shared.time.TimeProvider;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * System implementation of TimeProvider using actual system time.
 */
@Component
public class SystemTimeProvider implements TimeProvider {

    private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();

    @Override
    public Instant now() {
        return Instant.now();
    }

    @Override
    public LocalDateTime nowDateTime() {
        return LocalDateTime.now(DEFAULT_ZONE);
    }

    @Override
    public LocalDate nowDate() {
        return LocalDate.now(DEFAULT_ZONE);
    }
}

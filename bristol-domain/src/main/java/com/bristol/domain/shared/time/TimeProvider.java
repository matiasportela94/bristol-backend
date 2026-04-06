package com.bristol.domain.shared.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
}

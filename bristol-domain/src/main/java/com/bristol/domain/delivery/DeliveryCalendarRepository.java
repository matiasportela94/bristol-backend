package com.bristol.domain.delivery;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for DeliveryCalendar entity.
 */
public interface DeliveryCalendarRepository {

    /**
     * Save a delivery calendar entry (create or update).
     */
    DeliveryCalendar save(DeliveryCalendar deliveryCalendar);

    /**
     * Find delivery calendar by ID.
     */
    Optional<DeliveryCalendar> findById(DeliveryCalendarId calendarId);

    /**
     * Find delivery calendar by zone and date.
     */
    Optional<DeliveryCalendar> findByZoneAndDate(DeliveryZoneId zoneId, LocalDate date);

    /**
     * Find all delivery calendars for a zone.
     */
    List<DeliveryCalendar> findByZone(DeliveryZoneId zoneId);

    /**
     * Find delivery calendars for a zone within a date range.
     */
    List<DeliveryCalendar> findByZoneAndDateRange(
            DeliveryZoneId zoneId,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Find available delivery dates for a zone (with capacity).
     */
    List<DeliveryCalendar> findAvailableByZone(DeliveryZoneId zoneId, LocalDate fromDate);

    /**
     * Find fully booked delivery calendars.
     */
    List<DeliveryCalendar> findFullyBooked(LocalDate fromDate);

    /**
     * Check if a zone has capacity on a specific date.
     */
    boolean hasCapacity(DeliveryZoneId zoneId, LocalDate date);

    /**
     * Delete delivery calendar (should rarely be used).
     */
    void delete(DeliveryCalendarId calendarId);
}

package com.bristol.domain.delivery;

import com.bristol.domain.shared.exception.ValidationException;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;

/**
 * DeliveryCalendar entity.
 * Manages delivery capacity and bookings for a specific zone and date.
 */
@Getter
@Builder(toBuilder = true)
public class DeliveryCalendar {

    private final DeliveryCalendarId id;
    private final DeliveryZoneId deliveryZoneId;
    private final LocalDate deliveryDate;
    private final Integer capacity;
    private final Integer currentBookings;
    private final Instant createdAt;
    private final Instant updatedAt;

    /**
     * Create a new delivery calendar entry.
     */
    public static DeliveryCalendar create(
            DeliveryZoneId deliveryZoneId,
            LocalDate deliveryDate,
            Integer capacity,
            Instant now
    ) {
        validate(deliveryZoneId, deliveryDate, capacity);

        DeliveryCalendarId id = DeliveryCalendarId.generate();

        return DeliveryCalendar.builder()
                .id(id)
                .deliveryZoneId(deliveryZoneId)
                .deliveryDate(deliveryDate)
                .capacity(capacity)
                .currentBookings(0)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Book a delivery slot.
     */
    public DeliveryCalendar bookSlot(Instant now) {
        if (!hasAvailableCapacity()) {
            throw new ValidationException(
                    "No available delivery slots for " + deliveryDate + " in this zone"
            );
        }

        return this.toBuilder()
                .currentBookings(currentBookings + 1)
                .updatedAt(now)
                .build();
    }

    /**
     * Release a delivery slot (when delivery is cancelled).
     */
    public DeliveryCalendar releaseSlot(Instant now) {
        if (currentBookings <= 0) {
            throw new ValidationException("Cannot release slot, no bookings exist");
        }

        return this.toBuilder()
                .currentBookings(currentBookings - 1)
                .updatedAt(now)
                .build();
    }

    /**
     * Update capacity for this delivery date.
     */
    public DeliveryCalendar updateCapacity(Integer newCapacity, Instant now) {
        if (newCapacity == null || newCapacity < 0) {
            throw new ValidationException("Capacity must be zero or positive");
        }
        if (newCapacity < currentBookings) {
            throw new ValidationException(
                    "Cannot reduce capacity below current bookings (" + currentBookings + ")"
            );
        }

        return this.toBuilder()
                .capacity(newCapacity)
                .updatedAt(now)
                .build();
    }

    /**
     * Check if there is available capacity.
     */
    public boolean hasAvailableCapacity() {
        return currentBookings < capacity;
    }

    /**
     * Get number of available slots.
     */
    public int getAvailableSlots() {
        return Math.max(0, capacity - currentBookings);
    }

    /**
     * Get capacity utilization percentage.
     */
    public double getUtilizationPercentage() {
        if (capacity == 0) {
            return 0.0;
        }
        return (currentBookings * 100.0) / capacity;
    }

    /**
     * Check if this delivery date is in the past.
     */
    public boolean isPastDate(LocalDate today) {
        return deliveryDate.isBefore(today);
    }

    /**
     * Check if fully booked.
     */
    public boolean isFullyBooked() {
        return currentBookings >= capacity;
    }

    private static void validate(
            DeliveryZoneId deliveryZoneId,
            LocalDate deliveryDate,
            Integer capacity
    ) {
        if (deliveryZoneId == null) {
            throw new ValidationException("Delivery zone ID is required");
        }
        if (deliveryDate == null) {
            throw new ValidationException("Delivery date is required");
        }
        if (capacity == null || capacity < 0) {
            throw new ValidationException("Capacity must be zero or positive");
        }
    }
}

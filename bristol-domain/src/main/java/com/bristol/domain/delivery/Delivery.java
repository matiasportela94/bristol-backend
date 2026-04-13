package com.bristol.domain.delivery;

import com.bristol.domain.order.OrderId;
import com.bristol.domain.shared.exception.ValidationException;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Delivery aggregate root.
 * Represents the delivery of an order to a customer.
 */
@Getter
@Builder(toBuilder = true)
public class Delivery {

    private final DeliveryId id;
    private final Long deliveryNumber;
    private final OrderId orderId;
    private final DeliveryCalendarId deliveryCalendarId;
    private final DeliveryStatus status;
    private final LocalDate scheduledDate;
    private final LocalDate actualDeliveryDate; // nullable
    private final String driverNotes; // nullable
    private final String customerNotes; // nullable
    private final Instant createdAt;
    private final Instant updatedAt;

    /**
     * Schedule a new delivery.
     */
    public static Delivery schedule(
            OrderId orderId,
            DeliveryCalendarId deliveryCalendarId,
            LocalDate scheduledDate,
            String customerNotes,
            Instant now
    ) {
        validate(orderId, deliveryCalendarId, scheduledDate, now);

        DeliveryId id = DeliveryId.generate();

        return Delivery.builder()
                .id(id)
                .deliveryNumber(null)
                .orderId(orderId)
                .deliveryCalendarId(deliveryCalendarId)
                .status(DeliveryStatus.SCHEDULED)
                .scheduledDate(scheduledDate)
                .actualDeliveryDate(null)
                .driverNotes(null)
                .customerNotes(customerNotes)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Mark delivery as in transit.
     */
    public Delivery startTransit(String driverNotes, Instant now) {
        if (!status.equals(DeliveryStatus.SCHEDULED)) {
            throw new ValidationException("Only scheduled deliveries can start transit");
        }

        return this.toBuilder()
                .status(DeliveryStatus.IN_TRANSIT)
                .driverNotes(driverNotes)
                .updatedAt(now)
                .build();
    }

    /**
     * Mark delivery as completed.
     */
    public Delivery complete(LocalDate actualDeliveryDate, String driverNotes, Instant now) {
        if (!status.equals(DeliveryStatus.IN_TRANSIT)) {
            throw new ValidationException("Only in-transit deliveries can be completed");
        }
        if (actualDeliveryDate == null) {
            throw new ValidationException("Actual delivery date is required");
        }

        return this.toBuilder()
                .status(DeliveryStatus.DELIVERED)
                .actualDeliveryDate(actualDeliveryDate)
                .driverNotes(driverNotes)
                .updatedAt(now)
                .build();
    }

    /**
     * Mark delivery as failed.
     */
    public Delivery markAsFailed(String driverNotes, Instant now) {
        if (status.equals(DeliveryStatus.DELIVERED)) {
            throw new ValidationException("Delivered orders cannot be marked as failed");
        }

        return this.toBuilder()
                .status(DeliveryStatus.FAILED)
                .driverNotes(driverNotes)
                .updatedAt(now)
                .build();
    }

    /**
     * Reschedule delivery to a new date.
     */
    public Delivery reschedule(
            DeliveryCalendarId newCalendarId,
            LocalDate newScheduledDate,
            String reason,
            Instant now
    ) {
        if (status.equals(DeliveryStatus.DELIVERED)) {
            throw new ValidationException("Cannot reschedule delivered orders");
        }
        if (newCalendarId == null) {
            throw new ValidationException("New delivery calendar ID is required");
        }
        if (newScheduledDate == null) {
            throw new ValidationException("New scheduled date is required");
        }

        String updatedDriverNotes = (driverNotes != null ? driverNotes + " | " : "") +
                "Rescheduled: " + reason;

        return this.toBuilder()
                .deliveryCalendarId(newCalendarId)
                .scheduledDate(newScheduledDate)
                .status(DeliveryStatus.SCHEDULED)
                .driverNotes(updatedDriverNotes)
                .updatedAt(now)
                .build();
    }

    /**
     * Update driver notes.
     */
    public Delivery updateDriverNotes(String notes, Instant now) {
        return this.toBuilder()
                .driverNotes(notes)
                .updatedAt(now)
                .build();
    }

    /**
     * Check if delivery is overdue.
     */
    public boolean isOverdue(LocalDate today) {
        return status != DeliveryStatus.DELIVERED &&
               status != DeliveryStatus.FAILED &&
               scheduledDate.isBefore(today);
    }

    /**
     * Check if delivery is completed.
     */
    public boolean isCompleted() {
        return status == DeliveryStatus.DELIVERED;
    }

    /**
     * Check if delivery is in final state.
     */
    public boolean isFinal() {
        return status == DeliveryStatus.DELIVERED || status == DeliveryStatus.FAILED;
    }

    /**
     * Get days until scheduled delivery.
     */
    public long getDaysUntilDelivery(LocalDate today) {
        return today.until(scheduledDate).getDays();
    }

    private static void validate(
            OrderId orderId,
            DeliveryCalendarId deliveryCalendarId,
            LocalDate scheduledDate,
            Instant now
    ) {
        if (orderId == null) {
            throw new ValidationException("Order ID is required");
        }
        if (deliveryCalendarId == null) {
            throw new ValidationException("Delivery calendar ID is required");
        }
        if (scheduledDate == null) {
            throw new ValidationException("Scheduled date is required");
        }
    }
}

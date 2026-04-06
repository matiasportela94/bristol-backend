package com.bristol.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * JPA Entity for delivery_calendars table.
 */
@Entity
@Table(name = "delivery_calendars")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryCalendarEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "delivery_zone_id", nullable = false, columnDefinition = "UUID")
    private UUID deliveryZoneId;

    @Column(name = "delivery_date", nullable = false)
    private LocalDate deliveryDate;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "current_bookings", nullable = false)
    private Integer currentBookings;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (currentBookings == null) {
            currentBookings = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}

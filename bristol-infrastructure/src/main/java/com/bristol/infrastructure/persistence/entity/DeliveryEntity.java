package com.bristol.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * JPA Entity for deliveries table.
 */
@Entity
@Table(name = "deliveries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "delivery_number", nullable = false, unique = true)
    private Long deliveryNumber;

    @Column(name = "order_id", nullable = false, unique = true, columnDefinition = "UUID")
    private UUID orderId;

    @Column(name = "delivery_calendar_id", nullable = false, columnDefinition = "UUID")
    private UUID deliveryCalendarId;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false)
    private DeliveryStatusEnum deliveryStatus;

    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;

    @Column(name = "actual_delivery_date")
    private LocalDate actualDeliveryDate;

    @Column(name = "driver_notes", columnDefinition = "TEXT")
    private String driverNotes;

    @Column(name = "customer_notes", columnDefinition = "TEXT")
    private String customerNotes;

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
        if (deliveryStatus == null) {
            deliveryStatus = DeliveryStatusEnum.SCHEDULED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public enum DeliveryStatusEnum {
        SCHEDULED, IN_TRANSIT, DELIVERED, FAILED
    }
}

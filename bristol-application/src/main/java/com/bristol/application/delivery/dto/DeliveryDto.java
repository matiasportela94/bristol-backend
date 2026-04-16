package com.bristol.application.delivery.dto;

import com.bristol.domain.delivery.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDto {
    private String id;
    private Long deliveryNumber;
    private String orderId;
    private Long orderNumber;
    private String userEmail;
    private String distributorId;
    private String deliveryZoneId;
    private String address;
    private DeliveryStatus status;
    private LocalDate scheduledDate;
    private LocalDate actualDeliveryDate;
    private Instant scheduledAt;
    private Instant deliveredAt;
    private String driverNotes;
    private String customerNotes;
    private String notes;
    private boolean canMarkDelivered;
    private boolean canCancel;
    private Instant createdAt;
    private Instant updatedAt;
}

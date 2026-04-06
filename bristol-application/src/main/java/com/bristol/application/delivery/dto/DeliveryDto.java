package com.bristol.application.delivery.dto;

import com.bristol.domain.delivery.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDto {
    private String id;
    private String orderId;
    private String distributorId;
    private String deliveryZoneId;
    private String address;
    private DeliveryStatus status;
    private Instant scheduledAt;
    private Instant deliveredAt;
    private String notes;
    private Instant createdAt;
    private Instant updatedAt;
}

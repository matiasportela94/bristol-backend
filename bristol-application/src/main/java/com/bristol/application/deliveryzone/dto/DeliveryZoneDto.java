package com.bristol.application.deliveryzone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryZoneDto {
    private String id;
    private String name;
    private String description;
    private BigDecimal baseCost;
    private BigDecimal costPerKm;
    private Integer maxDistanceKm;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}

package com.bristol.application.deliveryzone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeliveryZoneRequest {
    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Base cost is required")
    @Positive(message = "Base cost must be positive")
    private BigDecimal baseCost;

    @NotNull(message = "Cost per km is required")
    @Positive(message = "Cost per km must be positive")
    private BigDecimal costPerKm;

    @NotNull(message = "Max distance is required")
    @Positive(message = "Max distance must be positive")
    private Integer maxDistanceKm;
}

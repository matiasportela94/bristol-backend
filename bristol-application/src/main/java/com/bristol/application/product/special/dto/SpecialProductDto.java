package com.bristol.application.product.special.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO for special product information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecialProductDto {

    private String id;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private String specialTypeId;
    private String notes;
    private boolean requiresQuote;
    private Integer stockQuantity;
    private Integer lowStockThreshold;
    private boolean featured;
    private Instant createdAt;
    private Instant updatedAt;
}

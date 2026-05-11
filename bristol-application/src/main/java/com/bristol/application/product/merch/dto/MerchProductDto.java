package com.bristol.application.product.merch.dto;

import com.bristol.domain.catalog.MerchCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO for merch product information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchProductDto {

    private String id;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private String merchTypeId;
    private MerchCategory merchCategory;
    private String material;
    private String brand;
    private Integer stockQuantity;
    private Integer lowStockThreshold;
    private boolean featured;
    private Instant createdAt;
    private Instant updatedAt;
}

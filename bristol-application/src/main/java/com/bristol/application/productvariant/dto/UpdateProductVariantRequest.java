package com.bristol.application.productvariant.dto;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for updating a product variant.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductVariantRequest {

    private String sku;

    private String size;

    private String color;

    @PositiveOrZero(message = "Additional price must be zero or positive")
    private BigDecimal additionalPrice;

    @PositiveOrZero(message = "Stock quantity must be zero or positive")
    private Integer stockQuantity;

    private String imageUrl;
}

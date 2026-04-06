package com.bristol.application.productvariant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for creating a new product variant.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductVariantRequest {

    @NotBlank(message = "Product ID is required")
    private String productId;

    private String sku;

    private String size;

    private String color;

    @PositiveOrZero(message = "Additional price must be zero or positive")
    private BigDecimal additionalPrice;

    @NotNull(message = "Stock quantity is required")
    @PositiveOrZero(message = "Stock quantity must be zero or positive")
    private Integer stockQuantity;

    private String imageUrl;
}

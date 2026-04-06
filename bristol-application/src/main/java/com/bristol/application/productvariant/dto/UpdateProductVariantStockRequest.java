package com.bristol.application.productvariant.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating product variant stock quantity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductVariantStockRequest {

    @NotNull(message = "Stock quantity is required")
    @PositiveOrZero(message = "Stock quantity must be zero or positive")
    private Integer stockQuantity;
}

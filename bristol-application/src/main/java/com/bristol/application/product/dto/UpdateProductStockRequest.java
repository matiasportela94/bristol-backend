package com.bristol.application.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductStockRequest {
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity must be zero or positive")
    private Integer stockQuantity;
}

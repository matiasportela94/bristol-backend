package com.bristol.application.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for order item in create request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {

    @NotBlank(message = "Product ID is required")
    private String productId;

    @NotBlank(message = "Product variant ID is required")
    private String productVariantId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private String itemDiscountCouponCode;
}

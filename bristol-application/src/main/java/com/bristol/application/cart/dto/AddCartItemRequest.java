package com.bristol.application.cart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddCartItemRequest {

    @NotBlank(message = "Product ID is required")
    private String productId;

    private String productVariantId;

    @Positive(message = "Quantity must be greater than zero")
    private Integer quantity;
}

package com.bristol.application.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartAdjustmentDto {
    private CartAdjustmentType type;
    private String itemId;
    private String productId;
    private String message;
    private String previousValue;
    private String currentValue;
}

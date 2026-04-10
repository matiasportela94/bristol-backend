package com.bristol.application.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {
    private String id;
    private String userId;
    private List<CartItemDto> items;
    private BigDecimal originalSubtotal;
    private BigDecimal productDiscountAmount;
    private BigDecimal subtotal;
    private BigDecimal orderDiscountAmount;
    private BigDecimal total;
    private Integer totalItems;
    private CartAppliedPromotionDto appliedOrderPromotion;
    private Instant createdAt;
    private Instant updatedAt;
}

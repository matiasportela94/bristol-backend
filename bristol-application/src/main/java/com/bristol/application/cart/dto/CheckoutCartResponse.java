package com.bristol.application.cart.dto;

import com.bristol.application.order.dto.OrderDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutCartResponse {
    private boolean checkoutSucceeded;
    private String message;
    private CartDto cart;
    private List<CartAdjustmentDto> adjustments;
    private OrderDto createdOrder;
}

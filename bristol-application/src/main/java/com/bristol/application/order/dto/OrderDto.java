package com.bristol.application.order.dto;

import com.bristol.domain.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for Order information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private String id;
    private Long orderNumber;
    private String userId;
    private String customerName;
    private String userEmail;
    private String distributorName;
    private OrderStatus status;
    private ShippingAddressDto shippingAddress;
    private List<OrderItemDto> items;
    private BigDecimal subtotal;
    private BigDecimal orderDiscountAmount;
    private BigDecimal shippingCost;
    private BigDecimal shippingDiscountAmount;
    private BigDecimal total;
    private boolean stockUpdated;
    private String notes;
    private LocalDate scheduledDelivery;
    private String deliveryStatus;
    private Instant orderDate;
    private Instant createdAt;
    private Instant updatedAt;
}

package com.bristol.application.order.dto;

import com.bristol.domain.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request DTO for filtering orders.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderFilterRequest {

    private String orderId;
    private OrderStatus status;
    private String distributorId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String userId;
}

package com.bristol.application.order.dto;

import com.bristol.domain.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request to filter orders with optional criteria.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterOrdersRequest {

    private OrderStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private String distributorId;
    private String userId;
}

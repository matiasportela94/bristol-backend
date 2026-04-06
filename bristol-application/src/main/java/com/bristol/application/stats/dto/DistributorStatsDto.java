package com.bristol.application.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistributorStatsDto {
    private String distributorId;
    private String email;
    private BigDecimal monthlySpent;
    private Integer monthlyOrders;
    private BigDecimal totalSpent;
    private Integer totalOrders;
    private Integer totalBeersOrdered;
    private Integer pendingDeliveries;
    private Integer pendingPayments;
    private Instant lastOrderDate;
    private BigDecimal lastOrderAmount;
    private Instant updatedAt;
}

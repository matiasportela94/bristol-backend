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
public class AdminDashboardStatsDto {
    private Integer totalOrders;
    private BigDecimal totalRevenue;
    private Integer pendingOrders;
    private Integer totalDistributors;
    private Integer pendingDistributors;
    private Instant updatedAt;
}

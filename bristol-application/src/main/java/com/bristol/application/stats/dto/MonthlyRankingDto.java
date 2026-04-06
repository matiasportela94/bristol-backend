package com.bristol.application.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyRankingDto {
    private Integer position;
    private String distributorId;
    private String distributorName;
    private BigDecimal monthlySpent;
    private Integer monthlyOrders;
    private BigDecimal monthlyPending;
    private BigDecimal previousMonthSpent;
    private String month;
}

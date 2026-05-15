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
public class CatalogStatsDto {
    private int totalProducts;
    private long totalUnitsInStock;   // beer: total cans from inventory; merch/special: product stock
    private BigDecimal totalValue;    // beer: cans × cheapest per-can price; others: stock × price
    private int lowStockCount;        // beer styles below threshold; other products below threshold
    private int outOfStockVariantsCount; // merch products with at least one variant at zero stock
}

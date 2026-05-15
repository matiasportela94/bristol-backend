package com.bristol.application.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeerCostAnalysisDto {

    private List<BeerStyleCostDto> styles;

    /** Sum of all styles' inventory value at PPP cost */
    private BigDecimal totalInventoryValueAtPpp;
    /** Sum of all styles' inventory value at FIFO cost */
    private BigDecimal totalInventoryValueAtFifo;
}

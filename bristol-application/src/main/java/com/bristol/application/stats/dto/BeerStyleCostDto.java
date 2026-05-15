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
public class BeerStyleCostDto {

    private String styleId;
    private String styleName;
    private String styleCode;
    private int totalCans;

    /** Lowest selling price per individual can across all products of this style */
    private BigDecimal lowestPricePerCan;

    /** Weighted-average cost per can across all batches with cost data */
    private BigDecimal pppCostPerCan;
    /** Cost per can of the oldest batch that has cost data (FIFO) */
    private BigDecimal fifoCostPerCan;

    private BigDecimal pppMarginPerCan;
    private BigDecimal pppMarginPercent;
    private BigDecimal fifoMarginPerCan;
    private BigDecimal fifoMarginPercent;

    /** Total inventory value at PPP cost (totalCans × pppCostPerCan) */
    private BigDecimal inventoryValueAtPpp;
    /** Total inventory value at FIFO cost (totalCans × fifoCostPerCan) */
    private BigDecimal inventoryValueAtFifo;

    private int batchCount;
    private int batchesWithCostCount;
}

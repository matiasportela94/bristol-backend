package com.bristol.application.product.beer.dto;

import com.bristol.domain.catalog.BeerStyleCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO for beer product information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeerProductDto {

    private String id;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private String beerStyleId;
    private BeerStyleCategory beerCategory;
    private BigDecimal abv;
    private Integer ibu;
    private Integer srm;
    private String origin;
    private String brewery;
    private Integer stockQuantity;
    private Integer lowStockThreshold;
    private boolean featured;
    private Instant createdAt;
    private Instant updatedAt;
}

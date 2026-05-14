package com.bristol.application.product.beer.dto;

import com.bristol.domain.catalog.BeerStyleCategory;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request to update a beer product.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBeerProductRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name cannot exceed 255 characters")
    private String name;

    private String description;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.01", message = "Base price must be positive")
    private BigDecimal basePrice;

    @NotBlank(message = "Beer style ID is required")
    private String beerStyleId;

    @NotNull(message = "Beer category is required")
    private BeerStyleCategory beerCategory;

    @DecimalMin(value = "0.0", inclusive = false, message = "ABV must be positive")
    private BigDecimal abv;

    @Min(value = 0, message = "IBU cannot be negative")
    private Integer ibu;

    @Min(value = 0, message = "SRM cannot be negative")
    private Integer srm;

    private String origin;

    private String brewery;

    @PositiveOrZero(message = "Stock quantity must be zero or positive")
    private Integer stockQuantity;

    @PositiveOrZero(message = "Low stock threshold must be zero or positive")
    private Integer lowStockThreshold;
}

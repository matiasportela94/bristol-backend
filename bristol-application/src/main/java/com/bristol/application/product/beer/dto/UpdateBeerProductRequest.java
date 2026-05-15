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

    private String origin;

    private String brewery;

    @Min(value = 1, message = "Cans per unit must be at least 1")
    private Integer cansPerUnit;

    @PositiveOrZero(message = "Stock quantity must be zero or positive")
    private Integer stockQuantity;

    @PositiveOrZero(message = "Low stock threshold must be zero or positive")
    private Integer lowStockThreshold;
}

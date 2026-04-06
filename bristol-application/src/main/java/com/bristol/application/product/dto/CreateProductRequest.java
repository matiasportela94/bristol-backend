package com.bristol.application.product.dto;

import com.bristol.domain.product.BeerType;
import com.bristol.domain.product.ProductCategory;
import com.bristol.domain.product.ProductSubcategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for creating a new product.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message = "Category is required")
    private ProductCategory category;

    private ProductSubcategory subcategory;

    private BeerType beerType;

    @PositiveOrZero(message = "Price must be zero or positive")
    private BigDecimal price;

    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    @Min(value = 0, message = "Low stock threshold cannot be negative")
    private Integer lowStockThreshold;

    private Boolean isFeatured;

    private List<@Valid ProductImageRequest> images;
}

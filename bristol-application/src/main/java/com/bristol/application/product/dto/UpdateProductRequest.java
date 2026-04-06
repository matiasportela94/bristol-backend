package com.bristol.application.product.dto;

import com.bristol.domain.product.BeerType;
import com.bristol.domain.product.ProductCategory;
import com.bristol.domain.product.ProductSubcategory;
import jakarta.validation.Valid;
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
 * DTO for updating an existing product.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message = "Category is required")
    private ProductCategory category;

    private ProductSubcategory subcategory;

    private BeerType beerType;

    @PositiveOrZero(message = "Price must be zero or positive")
    private BigDecimal price;

    private Boolean isFeatured;

    private List<@Valid ProductImageRequest> images;
}

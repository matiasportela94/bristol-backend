package com.bristol.application.product.dto;

import com.bristol.domain.product.ProductCategory;
import com.bristol.domain.product.BeerType;
import com.bristol.domain.product.ProductSubcategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for Product information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private String id;
    private String name;
    private String description;
    private ProductCategory category;
    private ProductSubcategory subcategory;
    private BeerType beerType;
    private BigDecimal price;
    private Integer stockQuantity;
    private Integer minStockLevel;
    private List<ProductImageDto> images;
    private boolean active;
    private boolean featured;
    private Double averageRating;
    private Integer reviewCount;
}

package com.bristol.application.product.dto;

import com.bristol.domain.product.ProductCategory;
import com.bristol.domain.product.BeerType;
import com.bristol.domain.product.ProductSubcategory;
import com.bristol.application.productvariant.dto.ProductVariantDto;
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
    private String beerStyleCode;
    private String beerStyleName;
    private BigDecimal abv;
    private BigDecimal ibu;
    private BigDecimal srm;
    private String brewery;
    private String origin;
    private Integer cansPerUnit;
    private BigDecimal price;
    private Integer stockQuantity;
    private Integer minStockLevel;
    private List<ProductVariantDto> variants;
    private List<ProductImageDto> images;
    private boolean active;
    private boolean featured;
    private Double averageRating;
    private Integer reviewCount;
    private List<ProductPromotionDto> promotions;
}

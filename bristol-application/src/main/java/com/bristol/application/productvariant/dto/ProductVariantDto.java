package com.bristol.application.productvariant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for ProductVariant information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantDto {

    private String id;
    private String productId;
    private String sku;
    private String size;
    private Integer sizeMl;
    private String color;
    private BigDecimal additionalPrice;
    private Integer stockQuantity;
    private String imageUrl;
    private boolean inStock;
}

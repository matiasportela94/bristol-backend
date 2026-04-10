package com.bristol.application.order.dto;

import com.bristol.domain.order.ProductType;
import com.bristol.domain.product.BeerType;
import com.bristol.domain.product.ProductCategory;
import com.bristol.domain.product.ProductSubcategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for Order Item information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {

    private String id;
    private String productId;
    private String productVariantId;
    private String productName;
    private ProductType productType;
    private BeerType beerType;
    private ProductCategory productCategory;
    private ProductSubcategory productSubcategory;
    private Integer quantity;
    private BigDecimal pricePerUnit;
    private BigDecimal itemDiscountAmount;
    private BigDecimal subtotal;
}

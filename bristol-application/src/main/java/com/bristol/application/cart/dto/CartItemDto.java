package com.bristol.application.cart.dto;

import com.bristol.domain.order.ProductType;
import com.bristol.domain.product.BeerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private String id;
    private String productId;
    private String productVariantId;
    private String productName;
    private ProductType productType;
    private BeerType beerType;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}

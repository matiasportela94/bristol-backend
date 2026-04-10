package com.bristol.application.coupon.dto;

import com.bristol.domain.coupon.CouponScopeType;
import com.bristol.domain.product.BeerType;
import com.bristol.domain.product.ProductCategory;
import com.bristol.domain.product.ProductSubcategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Structured coupon targeting payload for admin APIs.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponScopePayload {

    private CouponScopeType type;
    private Set<String> productIds;
    private Set<String> variantIds;
    private Set<ProductCategory> categories;
    private Set<ProductSubcategory> subcategories;
    private Set<BeerType> beerTypes;
}

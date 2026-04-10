package com.bristol.application.cart.dto;

import com.bristol.domain.coupon.CouponDiscountType;
import com.bristol.domain.coupon.CouponMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Customer-facing summary of an applied cart promotion.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartAppliedPromotionDto {
    private String id;
    private String title;
    private String description;
    private String badgeText;
    private String detailsText;
    private String code;
    private CouponMethod method;
    private CouponDiscountType discountType;
}

package com.bristol.application.product.dto;

import com.bristol.domain.coupon.CouponBenefitType;
import com.bristol.domain.coupon.CouponMethod;
import com.bristol.domain.coupon.CouponScopeType;
import com.bristol.domain.coupon.CouponValueType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Customer-facing promotion summary attached to catalog products.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPromotionDto {

    private String id;
    private String title;
    private String description;
    private String badgeText;
    private String detailsText;
    private String code;
    private CouponMethod method;
    private CouponScopeType scopeType;
    private CouponBenefitType benefitType;
    private CouponValueType valueType;
    private BigDecimal value;
    private Integer priority;
    private BigDecimal minimumAmount;
    private Integer minimumQuantity;
    private Integer buyQuantity;
    private Integer freeQuantity;
    private Integer payQuantity;
    private Integer thresholdQuantity;
    private String triggerProductId;
    private String triggerProductName;
    private Integer triggerQuantity;
}

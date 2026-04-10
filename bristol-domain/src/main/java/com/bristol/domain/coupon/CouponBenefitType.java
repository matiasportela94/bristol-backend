package com.bristol.domain.coupon;

/**
 * Explicit internal benefit classification derived from legacy coupon fields.
 */
public enum CouponBenefitType {
    ORDER_PERCENTAGE,
    ORDER_FIXED_AMOUNT,
    PRODUCT_PERCENTAGE,
    PRODUCT_FIXED_AMOUNT,
    SHIPPING_PERCENTAGE,
    SHIPPING_FIXED_AMOUNT,
    TRIGGERED_PRODUCT_DISCOUNT,
    BUY_X_GET_Y,
    BUY_X_FOR_Y,
    PERCENTAGE_ON_QUANTITY,
    QUANTITY_RULE
}

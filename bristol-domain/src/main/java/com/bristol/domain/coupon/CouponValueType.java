package com.bristol.domain.coupon;

/**
 * Coupon value type - percentage or fixed amount.
 * Matches the coupon_value_type ENUM in the database schema.
 */
public enum CouponValueType {
    PERCENTAGE,  // e.g., 10% off
    FIXED        // e.g., $500 off
}

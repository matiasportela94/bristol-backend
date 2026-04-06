package com.bristol.domain.coupon;

/**
 * Coupon method enumeration - how the coupon is applied.
 * Matches the coupon_method ENUM in the database schema.
 */
public enum CouponMethod {
    CODE,      // User enters a code
    AUTOMATIC  // Auto-applied based on conditions
}

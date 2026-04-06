package com.bristol.domain.coupon;

/**
 * Coupon status enumeration.
 * Matches the coupon_status ENUM in the database schema.
 */
public enum CouponStatus {
    ACTIVE,
    PAUSED,
    EXPIRED
}

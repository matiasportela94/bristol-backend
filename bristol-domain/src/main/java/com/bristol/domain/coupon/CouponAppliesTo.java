package com.bristol.domain.coupon;

/**
 * Coupon applies to - which items are eligible.
 * Matches the coupon_applies_to ENUM in the database schema.
 */
public enum CouponAppliesTo {
    ENTIRE_ORDER,        // Entire order (respecting discount_type)
    SPECIFIC_PRODUCTS,   // Only selected products
    COLLECTIONS          // Collections of products (future)
}

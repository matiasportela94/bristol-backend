package com.bristol.domain.coupon;

/**
 * Coupon discount type - what part of the order is discounted.
 * Matches the coupon_discount_type ENUM in the database schema.
 */
public enum CouponDiscountType {
    PRODUCT,   // Applies to specific products or collections
    ORDER,     // Applies to order subtotal
    SHIPPING   // Applies to shipping cost only
}

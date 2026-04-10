package com.bristol.domain.coupon;

/**
 * Explicit internal scope classification for coupon targeting.
 */
public enum CouponScopeType {
    ENTIRE_ORDER,
    SPECIFIC_PRODUCT,
    CATEGORY,
    SUBCATEGORY,
    BEER_TYPE,
    MANUAL_SELECTION,
    COLLECTION
}

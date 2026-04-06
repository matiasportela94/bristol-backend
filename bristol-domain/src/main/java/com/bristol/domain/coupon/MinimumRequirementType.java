package com.bristol.domain.coupon;

/**
 * Minimum requirement type enumeration for coupons.
 */
public enum MinimumRequirementType {
    NONE,            // No minimum requirement
    PURCHASE_AMOUNT, // Minimum purchase amount required
    ITEM_QUANTITY,   // Minimum item quantity required
    BOTH             // Both purchase amount and item quantity required
}

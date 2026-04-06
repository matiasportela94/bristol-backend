package com.bristol.domain.coupon;

/**
 * Schedule type enumeration for coupons.
 */
public enum CouponScheduleType {
    ALWAYS,       // Always active
    SCHEDULED,    // Active during a specific date range
    UNSCHEDULED   // Not scheduled yet
}

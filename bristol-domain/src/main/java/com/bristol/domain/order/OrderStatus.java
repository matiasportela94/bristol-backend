package com.bristol.domain.order;

/**
 * Order status enumeration.
 * Matches the order_status ENUM in the database schema.
 */
public enum OrderStatus {
    PENDING_PAYMENT,
    PAYMENT_IN_PROCESS,
    PAID,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    PAYMENT_FAILED
}

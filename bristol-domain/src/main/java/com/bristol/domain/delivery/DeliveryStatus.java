package com.bristol.domain.delivery;

/**
 * Delivery status enumeration.
 * Matches the delivery_status ENUM in the database schema.
 */
public enum DeliveryStatus {
    SCHEDULED,    // Delivery scheduled
    IN_TRANSIT,   // Out for delivery
    DELIVERED,    // Successfully delivered
    FAILED        // Delivery failed
}

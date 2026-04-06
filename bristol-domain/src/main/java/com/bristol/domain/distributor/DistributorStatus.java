package com.bristol.domain.distributor;

/**
 * Distributor status enumeration.
 * Matches the distributor_status ENUM in the database schema.
 */
public enum DistributorStatus {
    PENDING,   // Registration request pending review
    APPROVED,  // Distributor approved and active
    REJECTED   // Registration request rejected
}

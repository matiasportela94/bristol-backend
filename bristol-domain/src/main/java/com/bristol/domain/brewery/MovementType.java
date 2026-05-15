package com.bristol.domain.brewery;

public enum MovementType {
    BATCH_IN,    // New brewing batch added
    SALE_OUT,    // Sold via confirmed order
    ADJUSTMENT,  // Manual admin correction
    RETURN       // Order cancelled — cans restored
}

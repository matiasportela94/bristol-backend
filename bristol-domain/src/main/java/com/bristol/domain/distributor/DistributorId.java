package com.bristol.domain.distributor;

import com.bristol.domain.shared.valueobject.EntityId;

import java.util.UUID;

/**
 * Type-safe identifier for Distributor entities.
 */
public class DistributorId extends EntityId {

    public DistributorId(UUID value) {
        super(value);
    }

    public DistributorId(String value) {
        super(value);
    }

    public static DistributorId generate() {
        return new DistributorId(UUID.randomUUID());
    }
}

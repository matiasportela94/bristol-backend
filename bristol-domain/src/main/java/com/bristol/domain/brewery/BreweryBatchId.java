package com.bristol.domain.brewery;

import com.bristol.domain.shared.valueobject.EntityId;

import java.util.UUID;

public class BreweryBatchId extends EntityId {
    public BreweryBatchId(UUID value) {
        super(value);
    }

    public BreweryBatchId(String value) {
        super(value);
    }

    public static BreweryBatchId generate() {
        return new BreweryBatchId(UUID.randomUUID());
    }
}

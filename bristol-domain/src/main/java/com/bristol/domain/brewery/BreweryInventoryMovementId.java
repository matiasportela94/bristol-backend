package com.bristol.domain.brewery;

import com.bristol.domain.shared.valueobject.EntityId;

import java.util.UUID;

public class BreweryInventoryMovementId extends EntityId {

    public BreweryInventoryMovementId(UUID value) {
        super(value);
    }

    public static BreweryInventoryMovementId generate() {
        return new BreweryInventoryMovementId(UUID.randomUUID());
    }
}

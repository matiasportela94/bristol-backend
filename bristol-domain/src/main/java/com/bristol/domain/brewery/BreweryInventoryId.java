package com.bristol.domain.brewery;

import com.bristol.domain.shared.valueobject.EntityId;

import java.util.UUID;

public class BreweryInventoryId extends EntityId {
    public BreweryInventoryId(UUID value) {
        super(value);
    }

    public BreweryInventoryId(String value) {
        super(value);
    }

    public static BreweryInventoryId generate() {
        return new BreweryInventoryId(UUID.randomUUID());
    }
}

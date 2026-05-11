package com.bristol.domain.catalog;

import com.bristol.domain.shared.valueobject.EntityId;

import java.util.UUID;

public class BeerStyleId extends EntityId {
    public BeerStyleId(UUID value) {
        super(value);
    }

    public BeerStyleId(String value) {
        super(value);
    }

    public static BeerStyleId generate() {
        return new BeerStyleId(UUID.randomUUID());
    }
}

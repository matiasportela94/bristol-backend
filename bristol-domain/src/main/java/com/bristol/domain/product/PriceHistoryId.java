package com.bristol.domain.product;

import com.bristol.domain.shared.valueobject.EntityId;

import java.util.UUID;

public class PriceHistoryId extends EntityId {
    public PriceHistoryId(UUID value) {
        super(value);
    }

    public PriceHistoryId(String value) {
        super(value);
    }

    public static PriceHistoryId generate() {
        return new PriceHistoryId(UUID.randomUUID());
    }
}

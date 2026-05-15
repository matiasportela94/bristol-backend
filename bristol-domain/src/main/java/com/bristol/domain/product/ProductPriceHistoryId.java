package com.bristol.domain.product;

import com.bristol.domain.shared.valueobject.EntityId;

import java.util.UUID;

public class ProductPriceHistoryId extends EntityId {

    public ProductPriceHistoryId(UUID value) {
        super(value);
    }

    public static ProductPriceHistoryId generate() {
        return new ProductPriceHistoryId(UUID.randomUUID());
    }
}

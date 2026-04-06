package com.bristol.domain.product;

import com.bristol.domain.shared.valueobject.EntityId;

import java.util.UUID;

public class ProductId extends EntityId {
    public ProductId(UUID value) {
        super(value);
    }

    public ProductId(String value) {
        super(value);
    }

    public static ProductId generate() {
        return new ProductId(UUID.randomUUID());
    }
}

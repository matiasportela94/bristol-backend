package com.bristol.domain.product;

import com.bristol.domain.shared.valueobject.EntityId;

import java.util.UUID;

public class ProductImageId extends EntityId {
    public ProductImageId(UUID value) {
        super(value);
    }

    public ProductImageId(String value) {
        super(value);
    }

    public static ProductImageId generate() {
        return new ProductImageId(UUID.randomUUID());
    }
}

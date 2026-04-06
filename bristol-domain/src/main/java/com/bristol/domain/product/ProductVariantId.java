package com.bristol.domain.product;

import com.bristol.domain.shared.valueobject.EntityId;

import java.util.UUID;

public class ProductVariantId extends EntityId {
    public ProductVariantId(UUID value) {
        super(value);
    }

    public ProductVariantId(String value) {
        super(value);
    }

    public static ProductVariantId generate() {
        return new ProductVariantId(UUID.randomUUID());
    }
}

package com.bristol.domain.product;

import com.bristol.domain.shared.valueobject.EntityId;

import java.util.UUID;

public class ProductReviewId extends EntityId {
    public ProductReviewId(UUID value) {
        super(value);
    }

    public ProductReviewId(String value) {
        super(value);
    }

    public static ProductReviewId generate() {
        return new ProductReviewId(UUID.randomUUID());
    }
}

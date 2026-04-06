package com.bristol.domain.cart;

import com.bristol.domain.shared.valueobject.EntityId;

import java.util.UUID;

/**
 * Type-safe identifier for ShoppingCart entities.
 */
public class ShoppingCartId extends EntityId {

    public ShoppingCartId(UUID value) {
        super(value);
    }

    public ShoppingCartId(String value) {
        super(value);
    }

    public static ShoppingCartId generate() {
        return new ShoppingCartId(UUID.randomUUID());
    }
}

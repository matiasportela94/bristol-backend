package com.bristol.domain.cart;

import com.bristol.domain.shared.valueobject.EntityId;

import java.util.UUID;

/**
 * Type-safe identifier for CartItem entities.
 */
public class CartItemId extends EntityId {

    public CartItemId(UUID value) {
        super(value);
    }

    public CartItemId(String value) {
        super(value);
    }

    public static CartItemId generate() {
        return new CartItemId(UUID.randomUUID());
    }
}

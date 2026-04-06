package com.bristol.domain.order;

import com.bristol.domain.shared.valueobject.EntityId;

import java.util.UUID;

public class OrderItemId extends EntityId {
    public OrderItemId(UUID value) {
        super(value);
    }

    public OrderItemId(String value) {
        super(value);
    }

    public static OrderItemId generate() {
        return new OrderItemId(UUID.randomUUID());
    }
}

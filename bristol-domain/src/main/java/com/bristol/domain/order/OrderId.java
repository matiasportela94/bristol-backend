package com.bristol.domain.order;

import com.bristol.domain.shared.valueobject.EntityId;

import java.util.UUID;

public class OrderId extends EntityId {
    public OrderId(UUID value) {
        super(value);
    }

    public OrderId(String value) {
        super(value);
    }

    public static OrderId generate() {
        return new OrderId(UUID.randomUUID());
    }
}

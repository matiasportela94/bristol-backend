package com.bristol.domain.delivery;

import com.bristol.domain.shared.valueobject.EntityId;

import java.util.UUID;

public class DeliveryId extends EntityId {
    public DeliveryId(UUID value) {
        super(value);
    }

    public DeliveryId(String value) {
        super(value);
    }

    public static DeliveryId generate() {
        return new DeliveryId(UUID.randomUUID());
    }
}

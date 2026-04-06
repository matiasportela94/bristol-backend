package com.bristol.domain.delivery;

import com.bristol.domain.shared.valueobject.EntityId;

import java.util.UUID;

/**
 * Type-safe identifier for DeliveryZone entities.
 */
public class DeliveryZoneId extends EntityId {

    public DeliveryZoneId(UUID value) {
        super(value);
    }

    public DeliveryZoneId(String value) {
        super(value);
    }

    public static DeliveryZoneId generate() {
        return new DeliveryZoneId(UUID.randomUUID());
    }
}

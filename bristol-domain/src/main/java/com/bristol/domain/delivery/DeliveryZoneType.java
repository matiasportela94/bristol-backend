package com.bristol.domain.delivery;

import java.util.UUID;

/**
 * Enum representing delivery zone types with their fixed UUIDs.
 * These UUIDs match the ones in V3__insert_delivery_zones.sql migration.
 */
public enum DeliveryZoneType {
    SUR("11111111-1111-1111-1111-111111111111"),
    NORTE("22222222-2222-2222-2222-222222222222"),
    CENTRO("33333333-3333-3333-3333-333333333333");

    private final UUID uuid;

    DeliveryZoneType(String uuid) {
        this.uuid = UUID.fromString(uuid);
    }

    public UUID getUuid() {
        return uuid;
    }

    public DeliveryZoneId getDeliveryZoneId() {
        return new DeliveryZoneId(uuid);
    }

    /**
     * Get DeliveryZoneType from string name (case-insensitive).
     */
    public static DeliveryZoneType fromString(String name) {
        return valueOf(name.toUpperCase());
    }
}

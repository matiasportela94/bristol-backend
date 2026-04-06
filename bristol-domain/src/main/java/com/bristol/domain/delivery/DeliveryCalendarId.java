package com.bristol.domain.delivery;

import com.bristol.domain.shared.valueobject.EntityId;

import java.util.UUID;

public class DeliveryCalendarId extends EntityId {
    public DeliveryCalendarId(UUID value) {
        super(value);
    }

    public DeliveryCalendarId(String value) {
        super(value);
    }

    public static DeliveryCalendarId generate() {
        return new DeliveryCalendarId(UUID.randomUUID());
    }
}

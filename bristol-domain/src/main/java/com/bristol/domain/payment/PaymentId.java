package com.bristol.domain.payment;

import com.bristol.domain.shared.valueobject.EntityId;

import java.util.UUID;

public class PaymentId extends EntityId {
    public PaymentId(UUID value) {
        super(value);
    }

    public PaymentId(String value) {
        super(value);
    }

    public static PaymentId generate() {
        return new PaymentId(UUID.randomUUID());
    }
}

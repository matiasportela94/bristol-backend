package com.bristol.domain.coupon;

import com.bristol.domain.shared.valueobject.EntityId;

import java.util.UUID;

public class CouponId extends EntityId {
    public CouponId(UUID value) {
        super(value);
    }

    public CouponId(String value) {
        super(value);
    }

    public static CouponId generate() {
        return new CouponId(UUID.randomUUID());
    }
}

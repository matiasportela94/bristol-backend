package com.bristol.domain.coupon;

import com.bristol.domain.shared.valueobject.EntityId;

import java.util.UUID;

public class CouponRedemptionId extends EntityId {
    public CouponRedemptionId(UUID value) {
        super(value);
    }

    public CouponRedemptionId(String value) {
        super(value);
    }

    public static CouponRedemptionId generate() {
        return new CouponRedemptionId(UUID.randomUUID());
    }
}

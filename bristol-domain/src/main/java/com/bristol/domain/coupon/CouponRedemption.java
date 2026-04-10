package com.bristol.domain.coupon;

import com.bristol.domain.order.OrderId;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.UserId;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * Historical record of a coupon actually redeemed by a user on an order.
 */
@Getter
@Builder(toBuilder = true)
public class CouponRedemption {

    private final CouponRedemptionId id;
    private final CouponId couponId;
    private final OrderId orderId;
    private final UserId userId;
    private final Money appliedAmount;
    private final Instant appliedAt;

    public static CouponRedemption create(
            CouponId couponId,
            OrderId orderId,
            UserId userId,
            Money appliedAmount,
            Instant appliedAt
    ) {
        if (couponId == null) {
            throw new ValidationException("Coupon redemption requires a coupon");
        }
        if (orderId == null) {
            throw new ValidationException("Coupon redemption requires an order");
        }
        if (userId == null) {
            throw new ValidationException("Coupon redemption requires a user");
        }
        if (appliedAmount == null || appliedAmount.isNegative()) {
            throw new ValidationException("Coupon redemption amount must be zero or positive");
        }
        if (appliedAt == null) {
            throw new ValidationException("Coupon redemption requires an applied timestamp");
        }

        return CouponRedemption.builder()
                .id(CouponRedemptionId.generate())
                .couponId(couponId)
                .orderId(orderId)
                .userId(userId)
                .appliedAmount(appliedAmount)
                .appliedAt(appliedAt)
                .build();
    }
}

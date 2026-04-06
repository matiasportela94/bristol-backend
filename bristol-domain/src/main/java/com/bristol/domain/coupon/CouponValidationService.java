package com.bristol.domain.coupon;

import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.UserId;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * Domain service for coupon validation logic.
 */
@RequiredArgsConstructor
public class CouponValidationService {

    private final TimeProvider timeProvider;

    /**
     * Validate if a coupon can be used.
     */
    public void validateCoupon(Coupon coupon) {
        validateStatus(coupon);
        validateSchedule(coupon);
    }

    /**
     * Validate if a coupon can be applied to a specific order.
     */
    public void validateCouponForOrder(
            Coupon coupon,
            Money orderAmount,
            Integer itemQuantity,
            UserId userId
    ) {
        validateCoupon(coupon);
        validateMinimumRequirements(coupon, orderAmount, itemQuantity);
    }

    /**
     * Validate coupon status.
     */
    private void validateStatus(Coupon coupon) {
        if (!coupon.isActive()) {
            throw new ValidationException("Coupon is not active");
        }
    }

    /**
     * Validate coupon schedule (start/end dates).
     */
    private void validateSchedule(Coupon coupon) {
        LocalDateTime now = timeProvider.nowDateTime();
        LocalDateTime start = LocalDateTime.of(coupon.getStartDate(), coupon.getStartTime());

        if (now.isBefore(start)) {
            throw new ValidationException("Coupon is not yet valid");
        }

        if (coupon.isSetEndDate()) {
            LocalDateTime end = LocalDateTime.of(
                    coupon.getEndDate(),
                    coupon.getEndTime() != null ? coupon.getEndTime() : java.time.LocalTime.MAX
            );

            if (now.isAfter(end)) {
                throw new ValidationException("Coupon has expired");
            }
        }
    }

    /**
     * Validate minimum requirements.
     */
    private void validateMinimumRequirements(Coupon coupon, Money amount, Integer quantity) {
        if (coupon.getMinimumAmount() != null && amount.isLessThan(coupon.getMinimumAmount())) {
            throw new ValidationException(
                    String.format("Order amount must be at least %s", coupon.getMinimumAmount())
            );
        }

        if (coupon.getMinimumQuantity() != null && quantity < coupon.getMinimumQuantity()) {
            throw new ValidationException(
                    String.format("Order must contain at least %d items", coupon.getMinimumQuantity())
            );
        }
    }
}

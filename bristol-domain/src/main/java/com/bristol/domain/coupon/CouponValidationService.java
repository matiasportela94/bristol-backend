package com.bristol.domain.coupon;

import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.UserId;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Domain service for coupon validation logic.
 */
@RequiredArgsConstructor
public class CouponValidationService {

    private static final Pattern STRING_PATTERN = Pattern.compile("\"([^\"]+)\"");

    private final TimeProvider timeProvider;
    private final CouponRedemptionRepository couponRedemptionRepository;

    public CouponValidationService(TimeProvider timeProvider) {
        this(timeProvider, CouponRedemptionRepository.NO_OP);
    }

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
        validateCouponForOrder(coupon, orderAmount, itemQuantity, userId, null);
    }

    public void validateCouponForOrder(
            Coupon coupon,
            Money orderAmount,
            Integer itemQuantity,
            UserId userId,
            String userEmail
    ) {
        validateCoupon(coupon);
        validateCustomerEligibility(coupon, userEmail);
        validateUsageLimits(coupon, userId);
        validateMinimumRequirements(coupon, orderAmount, itemQuantity);
    }

    /**
     * Validate coupon rules available during lightweight checkout/code validation.
     */
    public void validateCouponForCheckout(Coupon coupon, Money orderAmount, UserId userId) {
        validateCouponForCheckout(coupon, orderAmount, userId, null);
    }

    public void validateCouponForCheckout(Coupon coupon, Money orderAmount, UserId userId, String userEmail) {
        validateCoupon(coupon);
        validateCustomerEligibility(coupon, userEmail);
        validateUsageLimits(coupon, userId);
        validateMinimumAmount(coupon, orderAmount);
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
        validateMinimumAmount(coupon, amount);
        validateMinimumQuantity(coupon, quantity);
    }

    private void validateMinimumAmount(Coupon coupon, Money amount) {
        if (coupon.getMinimumAmount() != null && amount != null && amount.isLessThan(coupon.getMinimumAmount())) {
            throw new ValidationException(
                    String.format("Order amount must be at least %s", coupon.getMinimumAmount())
            );
        }
    }

    private void validateMinimumQuantity(Coupon coupon, Integer quantity) {
        if (coupon.getMinimumQuantity() != null && quantity != null && quantity < coupon.getMinimumQuantity()) {
            throw new ValidationException(
                    String.format("Order must contain at least %d items", coupon.getMinimumQuantity())
            );
        }
    }

    private void validateUsageLimits(Coupon coupon, UserId userId) {
        long currentTotalUses = coupon.getUsedCount() != null ? coupon.getUsedCount() : 0;
        if (coupon.isLimitTotalUses() && coupon.getMaxTotalUses() != null) {
            currentTotalUses = Math.max(currentTotalUses, couponRedemptionRepository.countByCouponId(coupon.getId()));
        }

        if (coupon.isLimitTotalUses()
                && coupon.getMaxTotalUses() != null
                && currentTotalUses >= coupon.getMaxTotalUses()) {
            throw new ValidationException("Coupon has reached its total usage limit");
        }

        if (coupon.isLimitPerCustomer()
                && coupon.getMaxUsesPerCustomer() != null
                && userId != null
                && couponRedemptionRepository.countByCouponIdAndUserId(coupon.getId(), userId) >= coupon.getMaxUsesPerCustomer()) {
            throw new ValidationException("Coupon has reached its per-customer usage limit");
        }
    }

    private void validateCustomerEligibility(Coupon coupon, String userEmail) {
        if (!coupon.isForSpecificCustomers()) {
            return;
        }

        String normalizedEmail = normalizeEmail(userEmail);
        if (normalizedEmail == null) {
            throw new ValidationException("Coupon is only available for specific customers");
        }

        Matcher matcher = STRING_PATTERN.matcher(coupon.getSpecificCustomers() != null ? coupon.getSpecificCustomers() : "[]");
        while (matcher.find()) {
            if (normalizedEmail.equals(normalizeEmail(matcher.group(1)))) {
                return;
            }
        }

        throw new ValidationException("Coupon is only available for specific customers");
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }
}

package com.bristol.domain.coupon;

import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.user.UserId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponTest {

    @Test
    void reconfigureShouldAllowDraftStatusAndPriority() {
        Instant now = Instant.parse("2026-04-07T12:00:00Z");
        Coupon coupon = sampleCoupon(now);

        Coupon reconfigured = coupon.reconfigure(
                "Welcome Draft",
                "WELCOME10",
                "Draft coupon",
                CouponMethod.CODE,
                CouponDiscountType.ORDER,
                CouponValueType.PERCENTAGE,
                BigDecimal.TEN,
                CouponAppliesTo.ENTIRE_ORDER,
                "[]",
                CouponCustomerEligibility.EVERYONE,
                null,
                null,
                false,
                null,
                false,
                null,
                false,
                false,
                false,
                LocalDate.parse("2026-04-07"),
                LocalTime.MIDNIGHT,
                false,
                null,
                null,
                CouponStatus.DRAFT,
                7,
                CouponTriggerType.NONE,
                null,
                null,
                false,
                "[]",
                "{}",
                now
        );

        assertThat(reconfigured.getStatus()).isEqualTo(CouponStatus.DRAFT);
        assertThat(reconfigured.getPriority()).isEqualTo(7);
    }

    @Test
    void reconfigureShouldRejectNegativePriority() {
        Instant now = Instant.parse("2026-04-07T12:00:00Z");
        Coupon coupon = sampleCoupon(now);

        assertThatThrownBy(() -> coupon.reconfigure(
                "Welcome Draft",
                "WELCOME10",
                "Draft coupon",
                CouponMethod.CODE,
                CouponDiscountType.ORDER,
                CouponValueType.PERCENTAGE,
                BigDecimal.TEN,
                CouponAppliesTo.ENTIRE_ORDER,
                "[]",
                CouponCustomerEligibility.EVERYONE,
                null,
                null,
                false,
                null,
                false,
                null,
                false,
                false,
                false,
                LocalDate.parse("2026-04-07"),
                LocalTime.MIDNIGHT,
                false,
                null,
                null,
                CouponStatus.DRAFT,
                -1,
                CouponTriggerType.NONE,
                null,
                null,
                false,
                "[]",
                "{}",
                now
        )).isInstanceOf(ValidationException.class)
                .hasMessage("Priority cannot be negative");
    }

    @Test
    void getScopeShouldClassifySpecificProductSelection() {
        Instant now = Instant.parse("2026-04-07T12:00:00Z");
        Coupon coupon = sampleCoupon(now).reconfigure(
                "Products only",
                "PRODUCT10",
                "Product selection",
                CouponMethod.CODE,
                CouponDiscountType.PRODUCT,
                CouponValueType.PERCENTAGE,
                BigDecimal.TEN,
                CouponAppliesTo.SPECIFIC_PRODUCTS,
                "[{\"productId\":\"abc-123\"}]",
                CouponCustomerEligibility.EVERYONE,
                null,
                null,
                false,
                null,
                false,
                null,
                false,
                false,
                false,
                LocalDate.parse("2026-04-07"),
                LocalTime.MIDNIGHT,
                false,
                null,
                null,
                CouponStatus.ACTIVE,
                0,
                CouponTriggerType.NONE,
                null,
                null,
                false,
                "[]",
                "{}",
                now
        );

        assertThat(coupon.getScope().getType()).isEqualTo(CouponScopeType.SPECIFIC_PRODUCT);
        assertThat(coupon.getScope().hasStructuredSelection()).isTrue();
    }

    @Test
    void getBenefitShouldClassifyBuyXGetYRules() {
        Instant now = Instant.parse("2026-04-07T12:00:00Z");
        Coupon coupon = sampleCoupon(now).reconfigure(
                "2x1",
                "DOSPORUNO",
                "Buy x get y",
                CouponMethod.CODE,
                CouponDiscountType.PRODUCT,
                CouponValueType.FIXED,
                BigDecimal.ONE,
                CouponAppliesTo.SPECIFIC_PRODUCTS,
                "[{\"productId\":\"abc-123\"}]",
                CouponCustomerEligibility.EVERYONE,
                null,
                null,
                false,
                null,
                false,
                null,
                false,
                false,
                false,
                LocalDate.parse("2026-04-07"),
                LocalTime.MIDNIGHT,
                false,
                null,
                null,
                CouponStatus.ACTIVE,
                0,
                CouponTriggerType.BUY_X_GET_Y,
                "abc-123",
                "IPA",
                false,
                "[]",
                "{\"buyX\":2,\"getY\":1}",
                now
        );

        assertThat(coupon.getBenefit().getType()).isEqualTo(CouponBenefitType.BUY_X_GET_Y);
        assertThat(coupon.getBenefit().isAdvancedBenefit()).isTrue();
    }

    @Test
    void getBenefitShouldClassifyTriggeredProductDiscountRules() {
        Instant now = Instant.parse("2026-04-07T12:00:00Z");
        Coupon coupon = sampleCoupon(now).reconfigure(
                "Trigger discount",
                "TRIGGER50",
                "Triggered discount",
                CouponMethod.CODE,
                CouponDiscountType.PRODUCT,
                CouponValueType.PERCENTAGE,
                BigDecimal.valueOf(50),
                CouponAppliesTo.SPECIFIC_PRODUCTS,
                "[{\"productId\":\"target-123\"}]",
                CouponCustomerEligibility.EVERYONE,
                null,
                null,
                false,
                null,
                false,
                null,
                false,
                false,
                false,
                LocalDate.parse("2026-04-07"),
                LocalTime.MIDNIGHT,
                false,
                null,
                null,
                CouponStatus.ACTIVE,
                0,
                CouponTriggerType.PRODUCT_PURCHASE,
                "trigger-123",
                "Trigger product",
                false,
                "[]",
                "{\"triggerQuantity\":1}",
                now
        );

        assertThat(coupon.getBenefit().getType()).isEqualTo(CouponBenefitType.TRIGGERED_PRODUCT_DISCOUNT);
        assertThat(coupon.getBenefit().isAdvancedBenefit()).isTrue();
    }

    @Test
    void calculateDiscountShouldDelegateToNormalizedBenefit() {
        Coupon coupon = sampleCoupon(Instant.parse("2026-04-07T12:00:00Z"));

        assertThat(coupon.calculateDiscount(com.bristol.domain.shared.valueobject.Money.of(1000)).getAmount())
                .isEqualByComparingTo("100.00");
    }

    private static Coupon sampleCoupon(Instant now) {
        return Coupon.create(
                "Welcome",
                "WELCOME10",
                "Welcome coupon",
                CouponMethod.CODE,
                CouponDiscountType.ORDER,
                CouponValueType.PERCENTAGE,
                BigDecimal.TEN,
                CouponAppliesTo.ENTIRE_ORDER,
                LocalDate.parse("2026-04-07"),
                LocalTime.MIDNIGHT,
                UserId.generate(),
                now
        );
    }
}

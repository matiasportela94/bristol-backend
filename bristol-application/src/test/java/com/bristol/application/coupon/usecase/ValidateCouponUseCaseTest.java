package com.bristol.application.coupon.usecase;

import com.bristol.application.coupon.dto.CouponDto;
import com.bristol.application.coupon.dto.ValidateCouponRequest;
import com.bristol.domain.coupon.Coupon;
import com.bristol.domain.coupon.CouponAppliesTo;
import com.bristol.domain.coupon.CouponCustomerEligibility;
import com.bristol.domain.coupon.CouponDiscountType;
import com.bristol.domain.coupon.CouponMethod;
import com.bristol.domain.coupon.CouponRedemptionRepository;
import com.bristol.domain.coupon.CouponRepository;
import com.bristol.domain.coupon.CouponStatus;
import com.bristol.domain.coupon.CouponTriggerType;
import com.bristol.domain.coupon.CouponValueType;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.UserId;
import com.bristol.domain.user.UserRepository;
import com.bristol.domain.user.UserRole;
import com.bristol.domain.user.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ValidateCouponUseCaseTest {

    @Test
    void executeShouldRejectCouponWhenOrderTotalIsBelowMinimumAmount() {
        CouponRepository couponRepository = mock(CouponRepository.class);
        CouponRedemptionRepository couponRedemptionRepository = mock(CouponRedemptionRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        ValidateCouponUseCase useCase = new ValidateCouponUseCase(
                couponRepository,
                couponRedemptionRepository,
                new CouponMapper(),
                fixedTimeProvider(),
                userRepository
        );

        Coupon coupon = sampleCoupon()
                .setMinimumRequirements(Money.of(200), null, Instant.parse("2026-04-07T12:00:00Z"));
        when(couponRepository.findByCode("SAVE10")).thenReturn(Optional.of(coupon));

        ValidateCouponRequest request = ValidateCouponRequest.builder()
                .code("SAVE10")
                .orderTotal(BigDecimal.valueOf(150))
                .build();

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Order amount must be at least $200.00");
    }

    @Test
    void executeShouldRejectCouponWhenUserReachedPerCustomerLimit() {
        CouponRepository couponRepository = mock(CouponRepository.class);
        CouponRedemptionRepository couponRedemptionRepository = mock(CouponRedemptionRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        ValidateCouponUseCase useCase = new ValidateCouponUseCase(
                couponRepository,
                couponRedemptionRepository,
                new CouponMapper(),
                fixedTimeProvider(),
                userRepository
        );

        Coupon coupon = sampleCoupon()
                .setUsageLimits(false, null, true, 1, Instant.parse("2026-04-07T12:00:00Z"));
        UserId userId = UserId.generate();
        when(couponRepository.findByCode("SAVE10")).thenReturn(Optional.of(coupon));
        when(couponRedemptionRepository.countByCouponIdAndUserId(coupon.getId(), userId)).thenReturn(1L);
        when(userRepository.findById(userId)).thenReturn(Optional.of(User.create(
                "buyer@example.com",
                "hash",
                "Buyer",
                "User",
                UserRole.USER,
                Instant.parse("2026-04-07T12:00:00Z")
        )));

        ValidateCouponRequest request = ValidateCouponRequest.builder()
                .code("SAVE10")
                .orderTotal(BigDecimal.valueOf(250))
                .userId(userId.asString())
                .build();

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Coupon has reached its per-customer usage limit");
    }

    @Test
    void executeShouldReturnCouponWhenSharedValidationPasses() {
        CouponRepository couponRepository = mock(CouponRepository.class);
        CouponRedemptionRepository couponRedemptionRepository = mock(CouponRedemptionRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        ValidateCouponUseCase useCase = new ValidateCouponUseCase(
                couponRepository,
                couponRedemptionRepository,
                new CouponMapper(),
                fixedTimeProvider(),
                userRepository
        );

        Coupon coupon = sampleCoupon()
                .setMinimumRequirements(Money.of(100), null, Instant.parse("2026-04-07T12:00:00Z"));
        UserId userId = UserId.generate();
        when(couponRepository.findByCode("SAVE10")).thenReturn(Optional.of(coupon));
        when(couponRedemptionRepository.countByCouponIdAndUserId(any(), any())).thenReturn(0L);
        when(userRepository.findById(userId)).thenReturn(Optional.of(User.create(
                "buyer@example.com",
                "hash",
                "Buyer",
                "User",
                UserRole.USER,
                Instant.parse("2026-04-07T12:00:00Z")
        )));

        CouponDto result = useCase.execute(ValidateCouponRequest.builder()
                .code("SAVE10")
                .orderTotal(BigDecimal.valueOf(250))
                .userId(userId.asString())
                .build());

        assertThat(result.getCode()).isEqualTo("SAVE10");
        assertThat(result.getMinAmount()).isEqualByComparingTo("100.00");
    }

    @Test
    void executeShouldRejectCouponWhenAuthenticatedUserIsNotInSpecificCustomers() {
        CouponRepository couponRepository = mock(CouponRepository.class);
        CouponRedemptionRepository couponRedemptionRepository = mock(CouponRedemptionRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        ValidateCouponUseCase useCase = new ValidateCouponUseCase(
                couponRepository,
                couponRedemptionRepository,
                new CouponMapper(),
                fixedTimeProvider(),
                userRepository
        );

        Coupon coupon = sampleCoupon().reconfigure(
                "SAVE10",
                "SAVE10",
                "SAVE10",
                CouponMethod.CODE,
                CouponDiscountType.ORDER,
                CouponValueType.PERCENTAGE,
                BigDecimal.TEN,
                CouponAppliesTo.ENTIRE_ORDER,
                "[]",
                CouponCustomerEligibility.SPECIFIC_CUSTOMERS,
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
                "[\"ona@mail.com\"]",
                "{}",
                Instant.parse("2026-04-07T12:00:00Z")
        );

        UserId userId = UserId.generate();
        when(couponRepository.findByCode("SAVE10")).thenReturn(Optional.of(coupon));
        when(userRepository.findById(userId)).thenReturn(Optional.of(User.create(
                "other@mail.com",
                "hash",
                "Other",
                "User",
                UserRole.USER,
                Instant.parse("2026-04-07T12:00:00Z")
        )));

        ValidateCouponRequest request = ValidateCouponRequest.builder()
                .code("SAVE10")
                .orderTotal(BigDecimal.valueOf(250))
                .userId(userId.asString())
                .build();

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Coupon is only available for specific customers");
    }

    private static TimeProvider fixedTimeProvider() {
        return new TimeProvider() {
            @Override
            public Instant now() {
                return Instant.parse("2026-04-07T12:00:00Z");
            }

            @Override
            public LocalDateTime nowDateTime() {
                return LocalDateTime.parse("2026-04-07T09:00:00");
            }

            @Override
            public LocalDate nowDate() {
                return LocalDate.parse("2026-04-07");
            }
        };
    }

    private static Coupon sampleCoupon() {
        return Coupon.create(
                "SAVE10",
                "SAVE10",
                "SAVE10",
                CouponMethod.CODE,
                CouponDiscountType.ORDER,
                CouponValueType.PERCENTAGE,
                BigDecimal.TEN,
                CouponAppliesTo.ENTIRE_ORDER,
                LocalDate.parse("2026-04-07"),
                LocalTime.MIDNIGHT,
                UserId.generate(),
                Instant.parse("2026-04-07T12:00:00Z")
        ).reconfigure(
                "SAVE10",
                "SAVE10",
                "SAVE10",
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
                CouponStatus.ACTIVE,
                0,
                CouponTriggerType.NONE,
                null,
                null,
                false,
                "[]",
                "{}",
                Instant.parse("2026-04-07T12:00:00Z")
        );
    }
}

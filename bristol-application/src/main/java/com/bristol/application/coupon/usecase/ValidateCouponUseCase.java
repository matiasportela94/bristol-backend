package com.bristol.application.coupon.usecase;

import com.bristol.application.coupon.dto.CouponDto;
import com.bristol.application.coupon.dto.ValidateCouponRequest;
import com.bristol.domain.coupon.Coupon;
import com.bristol.domain.coupon.CouponRepository;
import com.bristol.domain.coupon.CouponRedemptionRepository;
import com.bristol.domain.coupon.CouponValidationService;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.user.UserId;
import com.bristol.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case to validate a coupon code.
 */
@Service
@RequiredArgsConstructor
public class ValidateCouponUseCase {

    private final CouponRepository couponRepository;
    private final CouponRedemptionRepository couponRedemptionRepository;
    private final CouponMapper couponMapper;
    private final TimeProvider timeProvider;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public CouponDto execute(ValidateCouponRequest request) {
        Coupon coupon = couponRepository.findByCode(request.getCode())
                .orElseThrow(() -> new ValidationException("Invalid coupon code: " + request.getCode()));

        UserId userId = parseUserId(request.getUserId());
        CouponValidationService couponValidationService =
                new CouponValidationService(timeProvider, couponRedemptionRepository);
        String userEmail = userId != null
                ? userRepository.findById(userId).map(user -> user.getEmail()).orElse(null)
                : null;

        couponValidationService.validateCouponForCheckout(
                coupon,
                Money.of(request.getOrderTotal()),
                userId,
                userEmail
        );

        return couponMapper.toDto(coupon);
    }

    private UserId parseUserId(String rawUserId) {
        if (rawUserId == null || rawUserId.isBlank()) {
            return null;
        }

        try {
            return new UserId(rawUserId.trim());
        } catch (IllegalArgumentException exception) {
            throw new ValidationException("Invalid user ID");
        }
    }
}

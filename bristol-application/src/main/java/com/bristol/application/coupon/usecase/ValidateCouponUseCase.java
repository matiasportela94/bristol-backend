package com.bristol.application.coupon.usecase;

import com.bristol.application.coupon.dto.CouponDto;
import com.bristol.application.coupon.dto.ValidateCouponRequest;
import com.bristol.domain.coupon.Coupon;
import com.bristol.domain.coupon.CouponRepository;
import com.bristol.domain.coupon.CouponStatus;
import com.bristol.domain.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Use case to validate a coupon code.
 */
@Service
@RequiredArgsConstructor
public class ValidateCouponUseCase {

    private final CouponRepository couponRepository;
    private final CouponMapper couponMapper;

    @Transactional(readOnly = true)
    public CouponDto execute(ValidateCouponRequest request) {
        // Find coupon by code
        Coupon coupon = couponRepository.findByCode(request.getCode())
                .orElseThrow(() -> new ValidationException("Invalid coupon code: " + request.getCode()));

        // Check if coupon is active
        if (coupon.getStatus() != CouponStatus.ACTIVE) {
            throw new ValidationException("Coupon is not active");
        }

        // Check if coupon has been deleted
        if (coupon.getDeletedAt() != null) {
            throw new ValidationException("Coupon is no longer available");
        }

        // Check date validity
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        if (today.isBefore(coupon.getStartDate())) {
            throw new ValidationException("Coupon is not yet valid");
        }

        if (coupon.isSetEndDate()) {
            if (today.isAfter(coupon.getEndDate())) {
                throw new ValidationException("Coupon has expired");
            }
            if (today.isEqual(coupon.getEndDate()) && now.isAfter(coupon.getEndTime())) {
                throw new ValidationException("Coupon has expired");
            }
        }

        // Additional validation can be added here (minimum amount, customer eligibility, etc.)

        return couponMapper.toDto(coupon);
    }
}

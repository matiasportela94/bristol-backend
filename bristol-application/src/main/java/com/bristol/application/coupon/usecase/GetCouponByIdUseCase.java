package com.bristol.application.coupon.usecase;

import com.bristol.application.coupon.dto.CouponDto;
import com.bristol.domain.coupon.Coupon;
import com.bristol.domain.coupon.CouponId;
import com.bristol.domain.coupon.CouponRepository;
import com.bristol.domain.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case to get coupon by ID.
 */
@Service
@RequiredArgsConstructor
public class GetCouponByIdUseCase {

    private final CouponRepository couponRepository;
    private final CouponMapper couponMapper;

    @Transactional(readOnly = true)
    public CouponDto execute(String couponId) {
        CouponId id = new CouponId(couponId);
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Coupon not found: " + couponId));
        return couponMapper.toDto(coupon);
    }
}

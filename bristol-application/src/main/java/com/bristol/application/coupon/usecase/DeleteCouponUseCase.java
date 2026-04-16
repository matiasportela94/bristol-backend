package com.bristol.application.coupon.usecase;

import com.bristol.domain.coupon.Coupon;
import com.bristol.domain.coupon.CouponId;
import com.bristol.domain.coupon.CouponRepository;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case to delete a coupon (soft delete).
 */
@Service
@RequiredArgsConstructor
public class DeleteCouponUseCase {

    private final CouponRepository couponRepository;
    private final TimeProvider timeProvider;

    @Transactional
    public void execute(String couponId) {
        CouponId id = new CouponId(couponId);
        var now = timeProvider.now();

        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Coupon not found: " + couponId));

        Coupon deletedCoupon = coupon.softDelete(now);
        couponRepository.save(deletedCoupon);
    }
}

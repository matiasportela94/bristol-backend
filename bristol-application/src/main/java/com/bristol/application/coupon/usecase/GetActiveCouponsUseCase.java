package com.bristol.application.coupon.usecase;

import com.bristol.application.coupon.dto.CouponDto;
import com.bristol.domain.coupon.Coupon;
import com.bristol.domain.coupon.CouponRepository;
import com.bristol.domain.coupon.CouponStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use case to get all active coupons.
 */
@Service
@RequiredArgsConstructor
public class GetActiveCouponsUseCase {

    private final CouponRepository couponRepository;
    private final CouponMapper couponMapper;

    @Transactional(readOnly = true)
    public List<CouponDto> execute() {
        List<Coupon> coupons = couponRepository.findByStatus(CouponStatus.ACTIVE);
        return coupons.stream()
                .map(couponMapper::toDto)
                .collect(Collectors.toList());
    }
}

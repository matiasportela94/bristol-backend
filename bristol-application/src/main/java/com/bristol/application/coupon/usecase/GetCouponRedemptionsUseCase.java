package com.bristol.application.coupon.usecase;

import com.bristol.application.coupon.dto.CouponRedemptionDto;
import com.bristol.domain.coupon.CouponId;
import com.bristol.domain.coupon.CouponRedemption;
import com.bristol.domain.coupon.CouponRedemptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Retrieves redemption history for a coupon.
 */
@Service
@RequiredArgsConstructor
public class GetCouponRedemptionsUseCase {

    private final CouponRedemptionRepository couponRedemptionRepository;

    @Transactional(readOnly = true)
    public List<CouponRedemptionDto> execute(String couponId) {
        return couponRedemptionRepository.findByCouponId(new CouponId(couponId)).stream()
                .map(this::toDto)
                .toList();
    }

    private CouponRedemptionDto toDto(CouponRedemption redemption) {
        return CouponRedemptionDto.builder()
                .id(redemption.getId().getValue().toString())
                .couponId(redemption.getCouponId().getValue().toString())
                .orderId(redemption.getOrderId().getValue().toString())
                .userId(redemption.getUserId().getValue().toString())
                .appliedAmount(redemption.getAppliedAmount().getAmount())
                .appliedAt(redemption.getAppliedAt())
                .build();
    }
}

package com.bristol.infrastructure.persistence.repository;

import com.bristol.domain.coupon.CouponId;
import com.bristol.domain.coupon.CouponRedemption;
import com.bristol.domain.coupon.CouponRedemptionId;
import com.bristol.domain.coupon.CouponRedemptionRepository;
import com.bristol.domain.order.OrderId;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.UserId;
import com.bristol.infrastructure.persistence.entity.CouponRedemptionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * JPA implementation for coupon redemption history.
 */
@Component
@RequiredArgsConstructor
public class CouponRedemptionRepositoryImpl implements CouponRedemptionRepository {

    private final JpaCouponRedemptionRepository jpaRepository;

    @Override
    public void saveAll(List<CouponRedemption> redemptions) {
        jpaRepository.saveAll(redemptions.stream()
                .map(this::toEntity)
                .toList());
    }

    @Override
    public List<CouponRedemption> findByOrderId(OrderId orderId) {
        return jpaRepository.findByOrderId(orderId.getValue()).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<CouponRedemption> findByCouponId(CouponId couponId) {
        return jpaRepository.findByCouponIdOrderByAppliedAtDesc(couponId.getValue()).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deleteByOrderId(OrderId orderId) {
        jpaRepository.deleteByOrderId(orderId.getValue());
    }

    @Override
    public long countByCouponId(CouponId couponId) {
        return jpaRepository.countByCouponId(couponId.getValue());
    }

    @Override
    public long countByCouponIdAndUserId(CouponId couponId, UserId userId) {
        return jpaRepository.countByCouponIdAndUserId(couponId.getValue(), userId.getValue());
    }

    private CouponRedemptionEntity toEntity(CouponRedemption redemption) {
        return CouponRedemptionEntity.builder()
                .id(redemption.getId().getValue())
                .couponId(redemption.getCouponId().getValue())
                .orderId(redemption.getOrderId().getValue())
                .userId(redemption.getUserId().getValue())
                .appliedAmount(redemption.getAppliedAmount().getAmount())
                .appliedAt(redemption.getAppliedAt())
                .build();
    }

    private CouponRedemption toDomain(CouponRedemptionEntity entity) {
        return CouponRedemption.builder()
                .id(new CouponRedemptionId(entity.getId()))
                .couponId(new CouponId(entity.getCouponId()))
                .orderId(new OrderId(entity.getOrderId()))
                .userId(new UserId(entity.getUserId()))
                .appliedAmount(Money.of(entity.getAppliedAmount()))
                .appliedAt(entity.getAppliedAt())
                .build();
    }
}

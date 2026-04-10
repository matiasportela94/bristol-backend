package com.bristol.infrastructure.persistence.repository;

import com.bristol.infrastructure.persistence.entity.CouponRedemptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaCouponRedemptionRepository extends JpaRepository<CouponRedemptionEntity, UUID> {

    List<CouponRedemptionEntity> findByOrderId(UUID orderId);

    List<CouponRedemptionEntity> findByCouponIdOrderByAppliedAtDesc(UUID couponId);

    void deleteByOrderId(UUID orderId);

    long countByCouponId(UUID couponId);

    long countByCouponIdAndUserId(UUID couponId, UUID userId);
}

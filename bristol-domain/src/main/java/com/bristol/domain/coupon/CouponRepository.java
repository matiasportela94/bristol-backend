package com.bristol.domain.coupon;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for Coupon aggregate.
 */
public interface CouponRepository {

    Coupon save(Coupon coupon);

    Optional<Coupon> findById(CouponId id);

    Optional<Coupon> findByCode(String code);

    List<Coupon> findAll();

    List<Coupon> findActive();

    List<Coupon> findByStatus(CouponStatus status);

    List<Coupon> findByDiscountType(CouponDiscountType discountType);

    List<Coupon> findAutomatic();

    void delete(CouponId id);
}

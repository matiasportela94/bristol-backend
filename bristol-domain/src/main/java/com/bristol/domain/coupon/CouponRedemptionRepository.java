package com.bristol.domain.coupon;

import com.bristol.domain.order.OrderId;
import com.bristol.domain.user.UserId;

import java.util.List;

/**
 * Repository port for coupon redemption history.
 */
public interface CouponRedemptionRepository {

    CouponRedemptionRepository NO_OP = new CouponRedemptionRepository() {
        @Override
        public void saveAll(List<CouponRedemption> redemptions) {
        }

        @Override
        public List<CouponRedemption> findByOrderId(OrderId orderId) {
            return List.of();
        }

        @Override
        public List<CouponRedemption> findByCouponId(CouponId couponId) {
            return List.of();
        }

        @Override
        public void deleteByOrderId(OrderId orderId) {
        }

        @Override
        public long countByCouponId(CouponId couponId) {
            return 0;
        }

        @Override
        public long countByCouponIdAndUserId(CouponId couponId, UserId userId) {
            return 0;
        }
    };

    void saveAll(List<CouponRedemption> redemptions);

    List<CouponRedemption> findByOrderId(OrderId orderId);

    List<CouponRedemption> findByCouponId(CouponId couponId);

    void deleteByOrderId(OrderId orderId);

    long countByCouponId(CouponId couponId);

    long countByCouponIdAndUserId(CouponId couponId, UserId userId);
}

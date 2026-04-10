package com.bristol.application.order.usecase;

import com.bristol.domain.coupon.CouponId;
import com.bristol.domain.coupon.CouponRedemption;
import com.bristol.domain.coupon.CouponRedemptionRepository;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderItem;
import com.bristol.domain.shared.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Persists coupon redemption history from paid orders and keeps usage counters synchronized.
 */
@Component
@RequiredArgsConstructor
public class CouponRedemptionApplicationService {

    private final CouponRedemptionRepository couponRedemptionRepository;
    private final com.bristol.domain.coupon.CouponRepository couponRepository;

    public void recordPaidOrderRedemptions(Order order, Instant now) {
        List<CouponRedemption> existingRedemptions = couponRedemptionRepository.findByOrderId(order.getId());
        List<CouponRedemption> newRedemptions = buildRedemptions(order, now);

        couponRedemptionRepository.deleteByOrderId(order.getId());
        if (!newRedemptions.isEmpty()) {
            couponRedemptionRepository.saveAll(newRedemptions);
        }

        syncUsageCounts(collectCouponIds(existingRedemptions, newRedemptions), now);
    }

    public void clearOrderRedemptions(Order order, Instant now) {
        List<CouponRedemption> existingRedemptions = couponRedemptionRepository.findByOrderId(order.getId());
        if (existingRedemptions.isEmpty()) {
            return;
        }

        couponRedemptionRepository.deleteByOrderId(order.getId());
        syncUsageCounts(collectCouponIds(existingRedemptions, List.of()), now);
    }

    private List<CouponRedemption> buildRedemptions(Order order, Instant now) {
        Map<CouponId, Money> appliedAmountsByCoupon = new LinkedHashMap<>();

        addRedemptionAmount(appliedAmountsByCoupon, order.getOrderDiscountCouponId(), order.getOrderDiscountAmount());
        addRedemptionAmount(appliedAmountsByCoupon, order.getShippingDiscountCouponId(), order.getShippingDiscountAmount());

        for (OrderItem item : order.getItems()) {
            addRedemptionAmount(appliedAmountsByCoupon, item.getItemDiscountCouponId(), item.getItemDiscountAmount());
        }

        List<CouponRedemption> redemptions = new ArrayList<>();
        for (Map.Entry<CouponId, Money> entry : appliedAmountsByCoupon.entrySet()) {
            if (entry.getValue().isZero()) {
                continue;
            }
            redemptions.add(CouponRedemption.create(
                    entry.getKey(),
                    order.getId(),
                    order.getUserId(),
                    entry.getValue(),
                    now
            ));
        }
        return redemptions;
    }

    private void addRedemptionAmount(Map<CouponId, Money> appliedAmountsByCoupon, CouponId couponId, Money amount) {
        if (couponId == null || amount == null || amount.isZero()) {
            return;
        }

        appliedAmountsByCoupon.merge(couponId, amount, Money::add);
    }

    private Set<CouponId> collectCouponIds(
            List<CouponRedemption> existingRedemptions,
            List<CouponRedemption> newRedemptions
    ) {
        Set<CouponId> couponIds = new LinkedHashSet<>();
        existingRedemptions.forEach(redemption -> couponIds.add(redemption.getCouponId()));
        newRedemptions.forEach(redemption -> couponIds.add(redemption.getCouponId()));
        return couponIds;
    }

    private void syncUsageCounts(Set<CouponId> couponIds, Instant now) {
        for (CouponId couponId : couponIds) {
            couponRepository.findById(couponId)
                    .map(coupon -> coupon.syncUsedCount(toIntCount(couponRedemptionRepository.countByCouponId(couponId)), now))
                    .ifPresent(couponRepository::save);
        }
    }

    private int toIntCount(long count) {
        return count > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) count;
    }
}

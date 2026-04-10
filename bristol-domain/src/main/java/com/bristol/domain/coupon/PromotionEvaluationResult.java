package com.bristol.domain.coupon;

import com.bristol.domain.order.OrderItem;

import java.util.List;
import java.util.Optional;

/**
 * Result of evaluating candidate promotions for an order snapshot.
 */
public record PromotionEvaluationResult(
        PromotionApplication orderPromotion,
        PromotionApplication shippingPromotion,
        java.util.List<PromotionApplication> productPromotions,
        List<OrderItem> repricedItems
) {

    public Optional<PromotionApplication> getOrderPromotion() {
        return Optional.ofNullable(orderPromotion);
    }

    public Optional<PromotionApplication> getShippingPromotion() {
        return Optional.ofNullable(shippingPromotion);
    }

    public Optional<PromotionApplication> getProductPromotion() {
        return getProductPromotions().stream().findFirst();
    }

    public List<PromotionApplication> getProductPromotions() {
        return productPromotions != null ? productPromotions : List.of();
    }

    public boolean containsCoupon(CouponId couponId) {
        return getOrderPromotion().map(PromotionApplication::coupon).map(Coupon::getId).filter(couponId::equals).isPresent()
                || getShippingPromotion().map(PromotionApplication::coupon).map(Coupon::getId).filter(couponId::equals).isPresent()
                || getProductPromotions().stream()
                .map(PromotionApplication::coupon)
                .map(Coupon::getId)
                .anyMatch(couponId::equals);
    }
}

package com.bristol.domain.order;

import com.bristol.domain.coupon.Coupon;
import com.bristol.domain.coupon.CouponDiscountType;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.valueobject.Money;

import java.util.List;

/**
 * Domain service for order calculations.
 * Handles complex order total calculations with multiple discount types.
 */
public class OrderCalculationService {

    /**
     * Calculate order-level discount amount.
     */
    public Money calculateOrderDiscount(Order order, Coupon coupon) {
        validateOrderCoupon(coupon);

        Money orderSubtotal = order.getSubtotal();
        return coupon.calculateDiscount(orderSubtotal);
    }

    /**
     * Calculate shipping discount amount.
     */
    public Money calculateShippingDiscount(Money shippingCost, Coupon coupon) {
        validateShippingCoupon(coupon);

        return coupon.calculateDiscount(shippingCost);
    }

    /**
     * Calculate item-level discount amount.
     */
    public Money calculateItemDiscount(OrderItem item, Coupon coupon) {
        validateProductCoupon(coupon);

        Money itemSubtotal = item.getOriginalSubtotal();
        return coupon.calculateDiscount(itemSubtotal);
    }

    /**
     * Calculate final order total with all discounts applied.
     */
    public Money calculateFinalTotal(
            List<OrderItem> items,
            Money orderDiscountAmount,
            Money shippingCost,
            Money shippingDiscountAmount
    ) {
        // Sum all item subtotals (already includes item-level discounts)
        Money itemsTotal = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(Money.zero(), Money::add);

        // Apply order-level discount
        Money totalAfterOrderDiscount = itemsTotal.subtract(orderDiscountAmount);

        // Add shipping cost
        Money totalWithShipping = totalAfterOrderDiscount.add(shippingCost);

        // Apply shipping discount
        Money finalTotal = totalWithShipping.subtract(shippingDiscountAmount);

        if (finalTotal.isNegative()) {
            throw new ValidationException("Order total cannot be negative");
        }

        return finalTotal;
    }

    /**
     * Recalculate order totals (used after applying discounts).
     */
    public Order recalculateOrder(Order order) {
        Money newSubtotal = order.getItems().stream()
                .map(OrderItem::getSubtotal)
                .reduce(Money.zero(), Money::add);

        Money newTotal = newSubtotal
                .subtract(order.getOrderDiscountAmount())
                .add(order.getShippingCost())
                .subtract(order.getShippingDiscountAmount());

        return order.toBuilder()
                .subtotal(newSubtotal)
                .total(newTotal)
                .build();
    }

    /**
     * Calculate total savings from all discounts.
     */
    public Money calculateTotalSavings(Order order) {
        // Calculate original order subtotal (sum of all items before discounts)
        Money originalItemsTotal = order.getItems().stream()
                .map(OrderItem::getOriginalSubtotal)
                .reduce(Money.zero(), Money::add);

        // Calculate item-level savings
        Money itemLevelSavings = order.getItems().stream()
                .map(OrderItem::getItemDiscountAmount)
                .reduce(Money.zero(), Money::add);

        // Add order-level discount
        Money orderLevelSavings = order.getOrderDiscountAmount();

        // Add shipping discount
        Money shippingLevelSavings = order.getShippingDiscountAmount();

        return itemLevelSavings
                .add(orderLevelSavings)
                .add(shippingLevelSavings);
    }

    /**
     * Validate if multiple coupons can be combined.
     */
    public void validateCouponCombination(List<Coupon> coupons) {
        boolean hasNonCombinableProductCoupon = coupons.stream()
                .filter(c -> c.getDiscountType() == CouponDiscountType.PRODUCT)
                .anyMatch(c -> !c.isCombineWithProductDiscounts());

        if (hasNonCombinableProductCoupon && coupons.size() > 1) {
            throw new ValidationException(
                    "This product coupon cannot be combined with other discounts"
            );
        }
    }

    private void validateOrderCoupon(Coupon coupon) {
        if (coupon.getDiscountType() != CouponDiscountType.ORDER) {
            throw new ValidationException(
                    "Only ORDER discount type coupons can be applied to order subtotal"
            );
        }
    }

    private void validateShippingCoupon(Coupon coupon) {
        if (coupon.getDiscountType() != CouponDiscountType.SHIPPING) {
            throw new ValidationException(
                    "Only SHIPPING discount type coupons can be applied to shipping cost"
            );
        }
    }

    private void validateProductCoupon(Coupon coupon) {
        if (coupon.getDiscountType() != CouponDiscountType.PRODUCT) {
            throw new ValidationException(
                    "Only PRODUCT discount type coupons can be applied to order items"
            );
        }
    }
}

package com.bristol.domain.order;

import com.bristol.domain.coupon.CouponId;
import com.bristol.domain.product.BeerType;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductVariantId;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.valueobject.Money;
import lombok.Builder;
import lombok.Getter;

/**
 * OrderItem entity.
 * Represents a line item in an order with product details, quantity, and pricing.
 */
@Getter
@Builder(toBuilder = true)
public class OrderItem {

    private final OrderItemId id;
    private final OrderId orderId;
    private final ProductId productId;
    private final ProductVariantId productVariantId; // nullable
    private final String productName;
    private final ProductType productType;
    private final BeerType beerType; // nullable, only for BEER type products
    private final Integer quantity;
    private final Money pricePerUnit;
    private final CouponId itemDiscountCouponId; // nullable
    private final Money itemDiscountAmount;
    private final Money subtotal;

    /**
     * Create a new order item.
     */
    public static OrderItem create(
            OrderId orderId,
            ProductId productId,
            ProductVariantId productVariantId,
            String productName,
            ProductType productType,
            BeerType beerType,
            Integer quantity,
            Money pricePerUnit
    ) {
        validate(orderId, productId, productName, productType, quantity, pricePerUnit);

        OrderItemId id = OrderItemId.generate();
        Money itemDiscountAmount = Money.zero();
        Money subtotal = pricePerUnit.multiply(quantity);

        return OrderItem.builder()
                .id(id)
                .orderId(orderId)
                .productId(productId)
                .productVariantId(productVariantId)
                .productName(productName)
                .productType(productType)
                .beerType(beerType)
                .quantity(quantity)
                .pricePerUnit(pricePerUnit)
                .itemDiscountCouponId(null)
                .itemDiscountAmount(itemDiscountAmount)
                .subtotal(subtotal)
                .build();
    }

    /**
     * Apply item-level discount from a coupon.
     */
    public OrderItem applyItemDiscount(CouponId couponId, Money discountAmount) {
        if (discountAmount.isNegative()) {
            throw new ValidationException("Item discount amount cannot be negative");
        }

        Money originalSubtotal = pricePerUnit.multiply(quantity);
        if (discountAmount.isGreaterThan(originalSubtotal)) {
            throw new ValidationException("Item discount cannot exceed item subtotal");
        }

        Money newSubtotal = originalSubtotal.subtract(discountAmount);

        return this.toBuilder()
                .itemDiscountCouponId(couponId)
                .itemDiscountAmount(discountAmount)
                .subtotal(newSubtotal)
                .build();
    }

    /**
     * Recalculate subtotal (used when price per unit changes).
     */
    public OrderItem recalculateSubtotal() {
        Money originalSubtotal = pricePerUnit.multiply(quantity);
        Money newSubtotal = originalSubtotal.subtract(itemDiscountAmount);

        return this.toBuilder()
                .subtotal(newSubtotal)
                .build();
    }

    /**
     * Rebind the item to the final order ID created by the aggregate root.
     */
    public OrderItem withOrderId(OrderId orderId) {
        if (orderId == null) {
            throw new ValidationException("Order ID is required");
        }

        return this.toBuilder()
                .orderId(orderId)
                .build();
    }

    /**
     * Get original subtotal before discounts.
     */
    public Money getOriginalSubtotal() {
        return pricePerUnit.multiply(quantity);
    }

    private static void validate(
            OrderId orderId,
            ProductId productId,
            String productName,
            ProductType productType,
            Integer quantity,
            Money pricePerUnit
    ) {
        if (orderId == null) {
            throw new ValidationException("Order ID is required");
        }
        if (productId == null) {
            throw new ValidationException("Product ID is required");
        }
        if (productName == null || productName.trim().isEmpty()) {
            throw new ValidationException("Product name is required");
        }
        if (productType == null) {
            throw new ValidationException("Product type is required");
        }
        if (quantity == null || quantity <= 0) {
            throw new ValidationException("Quantity must be greater than zero");
        }
        if (pricePerUnit == null || pricePerUnit.isNegative()) {
            throw new ValidationException("Price per unit must be zero or positive");
        }
    }
}

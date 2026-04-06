package com.bristol.domain.order;

import com.bristol.domain.coupon.CouponId;
import com.bristol.domain.distributor.DistributorId;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.UserId;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Order aggregate root.
 * Represents a customer order with items, discounts, shipping, and status.
 */
@Getter
@Builder(toBuilder = true)
public class Order {

    private final OrderId id;
    private final UserId userId;
    private final OrderStatus status;
    private final DistributorId distributorId; // nullable
    private final Instant orderDate;
    private final ShippingAddress shippingAddress;

    @Builder.Default
    private final List<OrderItem> items = new ArrayList<>();

    private final Money subtotal;
    private final CouponId orderDiscountCouponId; // nullable
    private final Money orderDiscountAmount;
    private final Money shippingCost;
    private final CouponId shippingDiscountCouponId; // nullable
    private final Money shippingDiscountAmount;
    private final Money total;

    private final boolean stockUpdated;
    private final String notes; // nullable

    private final Instant createdAt;
    private final Instant updatedAt;

    /**
     * Create a new order.
     */
    public static Order create(
            UserId userId,
            ShippingAddress shippingAddress,
            List<OrderItem> items,
            Money shippingCost,
            String notes,
            Instant now
    ) {
        validate(userId, shippingAddress, items, shippingCost);

        OrderId orderId = OrderId.generate();
        List<OrderItem> normalizedItems = items.stream()
                .map(item -> item.withOrderId(orderId))
                .collect(Collectors.toCollection(ArrayList::new));
        Money subtotal = calculateItemsSubtotal(items);
        Money total = subtotal.add(shippingCost);

        return Order.builder()
                .id(orderId)
                .userId(userId)
                .status(OrderStatus.PENDING_PAYMENT)
                .distributorId(null)
                .orderDate(now)
                .shippingAddress(shippingAddress)
                .items(normalizedItems)
                .subtotal(subtotal)
                .orderDiscountCouponId(null)
                .orderDiscountAmount(Money.zero())
                .shippingCost(shippingCost)
                .shippingDiscountCouponId(null)
                .shippingDiscountAmount(Money.zero())
                .total(total)
                .stockUpdated(false)
                .notes(notes)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Apply order-level discount.
     */
    public Order applyOrderDiscount(CouponId couponId, Money discountAmount, Instant now) {
        if (discountAmount.isNegative()) {
            throw new ValidationException("Order discount amount cannot be negative");
        }
        if (discountAmount.isGreaterThan(subtotal)) {
            throw new ValidationException("Order discount cannot exceed order subtotal");
        }

        Money newTotal = subtotal
                .subtract(discountAmount)
                .add(shippingCost)
                .subtract(shippingDiscountAmount);

        return this.toBuilder()
                .orderDiscountCouponId(couponId)
                .orderDiscountAmount(discountAmount)
                .total(newTotal)
                .updatedAt(now)
                .build();
    }

    /**
     * Apply shipping discount.
     */
    public Order applyShippingDiscount(CouponId couponId, Money discountAmount, Instant now) {
        if (discountAmount.isNegative()) {
            throw new ValidationException("Shipping discount amount cannot be negative");
        }
        if (discountAmount.isGreaterThan(shippingCost)) {
            throw new ValidationException("Shipping discount cannot exceed shipping cost");
        }

        Money newTotal = subtotal
                .subtract(orderDiscountAmount)
                .add(shippingCost)
                .subtract(discountAmount);

        return this.toBuilder()
                .shippingDiscountCouponId(couponId)
                .shippingDiscountAmount(discountAmount)
                .total(newTotal)
                .updatedAt(now)
                .build();
    }

    /**
     * Assign order to a distributor.
     */
    public Order assignToDistributor(DistributorId distributorId, Instant now) {
        if (distributorId == null) {
            throw new ValidationException("Distributor ID is required");
        }
        if (!status.equals(OrderStatus.PAID)) {
            throw new ValidationException("Only paid orders can be assigned to distributors");
        }

        return this.toBuilder()
                .distributorId(distributorId)
                .updatedAt(now)
                .build();
    }

    /**
     * Mark order as paid.
     */
    public Order markAsPaid(Instant now) {
        if (!status.equals(OrderStatus.PENDING_PAYMENT) && !status.equals(OrderStatus.PAYMENT_IN_PROCESS)) {
            throw new ValidationException("Only orders awaiting payment can be marked as paid");
        }

        return this.toBuilder()
                .status(OrderStatus.PAID)
                .updatedAt(now)
                .build();
    }

    /**
     * Mark order as payment in process.
     */
    public Order markPaymentInProcess(Instant now) {
        if (!status.equals(OrderStatus.PENDING_PAYMENT)) {
            throw new ValidationException("Only pending payment orders can move to payment in process");
        }

        return this.toBuilder()
                .status(OrderStatus.PAYMENT_IN_PROCESS)
                .updatedAt(now)
                .build();
    }

    /**
     * Mark order as payment failed.
     */
    public Order markPaymentFailed(Instant now) {
        if (!status.equals(OrderStatus.PENDING_PAYMENT) && !status.equals(OrderStatus.PAYMENT_IN_PROCESS)) {
            throw new ValidationException("Only orders awaiting payment can be marked as payment failed");
        }

        return this.toBuilder()
                .status(OrderStatus.PAYMENT_FAILED)
                .updatedAt(now)
                .build();
    }

    /**
     * Start processing the order.
     */
    public Order startProcessing(Instant now) {
        if (!status.equals(OrderStatus.PAID)) {
            throw new ValidationException("Only paid orders can be processed");
        }

        return this.toBuilder()
                .status(OrderStatus.PROCESSING)
                .updatedAt(now)
                .build();
    }

    /**
     * Mark order as shipped.
     */
    public Order markAsShipped(Instant now) {
        if (!status.equals(OrderStatus.PROCESSING)) {
            throw new ValidationException("Only processing orders can be shipped");
        }

        return this.toBuilder()
                .status(OrderStatus.SHIPPED)
                .updatedAt(now)
                .build();
    }

    /**
     * Mark order as delivered.
     */
    public Order markAsDelivered(Instant now) {
        if (!status.equals(OrderStatus.SHIPPED)) {
            throw new ValidationException("Only shipped orders can be delivered");
        }

        return this.toBuilder()
                .status(OrderStatus.DELIVERED)
                .updatedAt(now)
                .build();
    }

    /**
     * Cancel the order.
     */
    public Order cancel(Instant now) {
        if (status.equals(OrderStatus.DELIVERED)) {
            throw new ValidationException("Delivered orders cannot be cancelled");
        }
        if (status.equals(OrderStatus.CANCELLED)) {
            throw new ValidationException("Order is already cancelled");
        }

        return this.toBuilder()
                .status(OrderStatus.CANCELLED)
                .updatedAt(now)
                .build();
    }

    /**
     * Mark stock as updated (to prevent duplicate stock deductions).
     */
    public Order markStockAsUpdated(Instant now) {
        if (stockUpdated) {
            throw new ValidationException("Stock already updated for this order");
        }

        return this.toBuilder()
                .stockUpdated(true)
                .updatedAt(now)
                .build();
    }

    /**
     * Get total number of items in the order.
     */
    public int getTotalItemCount() {
        return items.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }

    /**
     * Get total number of beer items.
     */
    public int getTotalBeerCount() {
        return items.stream()
                .filter(item -> item.getProductType() == ProductType.BEER)
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }

    /**
     * Get order items as unmodifiable list.
     */
    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Calculate final total with all discounts applied.
     */
    public Money getFinalTotal() {
        return subtotal
                .subtract(orderDiscountAmount)
                .add(shippingCost)
                .subtract(shippingDiscountAmount);
    }

    /**
     * Check if order can be cancelled.
     */
    public boolean isCancellable() {
        return !status.equals(OrderStatus.DELIVERED)
                && !status.equals(OrderStatus.CANCELLED)
                && !status.equals(OrderStatus.PAYMENT_FAILED);
    }

    /**
     * Check if order is in a final state.
     */
    public boolean isFinal() {
        return status.equals(OrderStatus.DELIVERED)
                || status.equals(OrderStatus.CANCELLED)
                || status.equals(OrderStatus.PAYMENT_FAILED);
    }

    private static Money calculateItemsSubtotal(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(Money.zero(), Money::add);
    }

    private static void validate(
            UserId userId,
            ShippingAddress shippingAddress,
            List<OrderItem> items,
            Money shippingCost
    ) {
        if (userId == null) {
            throw new ValidationException("User ID is required");
        }
        if (shippingAddress == null) {
            throw new ValidationException("Shipping address is required");
        }
        if (items == null || items.isEmpty()) {
            throw new ValidationException("Order must have at least one item");
        }
        if (shippingCost == null || shippingCost.isNegative()) {
            throw new ValidationException("Shipping cost must be zero or positive");
        }
    }
}

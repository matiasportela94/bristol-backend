package com.bristol.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * JPA Entity for orders table.
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "order_number", nullable = false, unique = true)
    private Long orderNumber;

    @Column(name = "user_id", nullable = false, columnDefinition = "UUID")
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatusEnum orderStatus;

    @Column(name = "distributor_id", columnDefinition = "UUID")
    private UUID distributorId;

    @Column(name = "order_date", nullable = false)
    private Instant orderDate;

    @Column(name = "shipping_address_line1", nullable = false, length = 255)
    private String shippingAddressLine1;

    @Column(name = "shipping_address_line2", length = 255)
    private String shippingAddressLine2;

    @Column(name = "shipping_city", nullable = false, length = 100)
    private String shippingCity;

    @Column(name = "shipping_province", nullable = false, length = 100)
    private String shippingProvince;

    @Column(name = "shipping_postal_code", length = 20)
    private String shippingPostalCode;

    @Column(name = "delivery_zone_id", nullable = false, columnDefinition = "UUID")
    private UUID deliveryZoneId;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @Column(name = "order_discount_coupon_id", columnDefinition = "UUID")
    private UUID orderDiscountCouponId;

    @Column(name = "order_discount_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal orderDiscountAmount;

    @Column(name = "shipping_cost", precision = 10, scale = 2, nullable = false)
    private BigDecimal shippingCost;

    @Column(name = "shipping_discount_coupon_id", columnDefinition = "UUID")
    private UUID shippingDiscountCouponId;

    @Column(name = "shipping_discount_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal shippingDiscountAmount;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal total;

    @Column(name = "stock_updated", nullable = false)
    private Boolean stockUpdated;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (orderStatus == null) {
            orderStatus = OrderStatusEnum.PENDING_PAYMENT;
        }
        if (stockUpdated == null) {
            stockUpdated = false;
        }
        if (orderDiscountAmount == null) {
            orderDiscountAmount = BigDecimal.ZERO;
        }
        if (shippingDiscountAmount == null) {
            shippingDiscountAmount = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public enum OrderStatusEnum {
        PENDING_PAYMENT, PAYMENT_IN_PROCESS, PAID, PROCESSING, SHIPPED, DELIVERED, CANCELLED, PAYMENT_FAILED
    }
}

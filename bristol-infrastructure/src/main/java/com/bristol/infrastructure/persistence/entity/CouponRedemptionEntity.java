package com.bristol.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity for coupon redemption history.
 */
@Entity
@Table(name = "coupon_redemptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponRedemptionEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "coupon_id", nullable = false, columnDefinition = "UUID")
    private UUID couponId;

    @Column(name = "order_id", nullable = false, columnDefinition = "UUID")
    private UUID orderId;

    @Column(name = "user_id", nullable = false, columnDefinition = "UUID")
    private UUID userId;

    @Column(name = "applied_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal appliedAmount;

    @Column(name = "applied_at", nullable = false)
    private Instant appliedAt;
}

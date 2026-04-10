package com.bristol.application.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Admin-facing coupon redemption history item.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponRedemptionDto {

    private String id;
    private String couponId;
    private String orderId;
    private String userId;
    private BigDecimal appliedAmount;
    private Instant appliedAt;
}

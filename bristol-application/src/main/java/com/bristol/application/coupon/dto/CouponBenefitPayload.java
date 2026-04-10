package com.bristol.application.coupon.dto;

import com.bristol.domain.coupon.CouponBenefitType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Structured coupon benefit payload for admin APIs.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponBenefitPayload {

    private CouponBenefitType type;
    private Integer buyQuantity;
    private Integer freeQuantity;
    private Integer payQuantity;
    private Integer thresholdQuantity;
    private Integer triggerQuantity;
    private Boolean advanced;
}

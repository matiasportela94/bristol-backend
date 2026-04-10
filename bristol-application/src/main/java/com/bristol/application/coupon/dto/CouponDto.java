package com.bristol.application.coupon.dto;

import com.bristol.domain.coupon.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for Coupon information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponDto {

    private String id;
    private String title;
    private String code;
    private String description;
    private CouponMethod method;
    private CouponDiscountType discountType;
    private CouponValueType valueType;
    private BigDecimal value;
    private CouponAppliesTo appliesTo;
    private String selectedItems;
    private CouponCustomerEligibility customerEligibility;
    private BigDecimal minAmount;
    private Integer minQuantity;
    private boolean limitTotalUses;
    private Integer maxTotalUses;
    private boolean limitPerCustomer;
    private Integer maxUsesPerCustomer;
    private boolean combineWithProduct;
    private boolean combineWithOrder;
    private boolean combineWithShipping;
    private LocalDate startDate;
    private LocalTime startTime;
    private boolean setEndDate;
    private LocalDate endDate;
    private LocalTime endTime;
    private CouponStatus status;
    private Integer priority;
    private Integer usedCount;
    private CouponTriggerType triggerType;
    private String triggerProductId;
    private String triggerProductName;
    private boolean appliesToFutureOrders;
    private String specificCustomers;
    private String ruleConfig;
    private CouponScopePayload scope;
    private CouponBenefitPayload benefit;
    private Instant createdAt;
    private Instant updatedAt;
}

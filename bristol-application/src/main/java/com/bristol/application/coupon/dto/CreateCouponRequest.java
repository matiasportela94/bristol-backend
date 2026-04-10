package com.bristol.application.coupon.dto;

import com.bristol.domain.coupon.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Request DTO for creating a coupon.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCouponRequest {

    private String title;

    private String code;

    private String description;

    @NotNull(message = "Method is required")
    private CouponMethod method;

    @NotNull(message = "Discount type is required")
    private CouponDiscountType discountType;

    @NotNull(message = "Value type is required")
    private CouponValueType valueType;

    @NotNull(message = "Value is required")
    @Positive(message = "Value must be positive")
    private BigDecimal value;

    @NotNull(message = "Applies to is required")
    private CouponAppliesTo appliesTo;

    private String selectedItems;

    @Builder.Default
    private CouponCustomerEligibility customerEligibility = CouponCustomerEligibility.EVERYONE;

    private BigDecimal minAmount;

    private Integer minQuantity;

    @Builder.Default
    private boolean limitTotalUses = false;

    private Integer maxTotalUses;

    @Builder.Default
    private boolean limitPerCustomer = false;

    private Integer maxUsesPerCustomer;

    @Builder.Default
    private boolean combineWithProduct = false;

    @Builder.Default
    private boolean combineWithOrder = false;

    @Builder.Default
    private boolean combineWithShipping = false;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalTime startTime;

    @Builder.Default
    private boolean setEndDate = false;

    private LocalDate endDate;

    private LocalTime endTime;

    private CouponStatus status;

    @PositiveOrZero(message = "Priority must be zero or positive")
    @Builder.Default
    private Integer priority = 0;

    @Builder.Default
    private CouponTriggerType triggerType = CouponTriggerType.NONE;

    private String triggerProductId;

    private String triggerProductName;

    @Builder.Default
    private boolean appliesToFutureOrders = false;

    private String specificCustomers;

    private String ruleConfig;

    private CouponScopePayload scope;

    private CouponBenefitPayload benefit;

    private String createdBy;
}

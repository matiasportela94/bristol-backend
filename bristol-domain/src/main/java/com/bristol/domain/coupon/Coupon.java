package com.bristol.domain.coupon;

import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.UserId;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Coupon aggregate root.
 * Represents a discount/coupon with advanced rules and conditions.
 */
@Getter
@Builder(toBuilder = true)
public class Coupon {

    private final CouponId id;
    private final String title;
    private final String code;
    private final String description;

    // Method & Type
    private final CouponMethod method;
    private final CouponDiscountType discountType;
    private final CouponValueType valueType;
    private final BigDecimal value;

    // Applies to
    private final CouponAppliesTo appliesTo;
    private final String selectedItems; // JSON string

    // Customer eligibility
    private final CouponCustomerEligibility customerEligibility;
    private final Money minimumAmount;
    private final Integer minimumQuantity;

    // Usage limits
    private final boolean limitTotalUses;
    private final Integer maxTotalUses;
    private final boolean limitPerCustomer;
    private final Integer maxUsesPerCustomer;

    // Combinability (granular control)
    private final boolean combineWithProductDiscounts;
    private final boolean combineWithOrderDiscounts;
    private final boolean combineWithShippingDiscounts;

    // Schedule
    private final LocalDate startDate;
    private final LocalTime startTime;
    private final boolean setEndDate;
    private final LocalDate endDate;
    private final LocalTime endTime;

    // Status
    private final CouponStatus status;
    private final Integer usedCount;
    private final Instant deletedAt;

    // Trigger-based discounts
    private final CouponTriggerType triggerType;
    private final String triggerProductId;
    private final String triggerProductName;
    private final Integer triggerQuantity;
    private final boolean appliesToFutureOrders;
    private final String specificCustomers; // JSON string
    private final String ruleConfig; // JSON string

    // Audit
    private final UserId createdBy;
    private final Instant createdAt;
    private final Instant updatedAt;

    /**
     * Factory method to create a new coupon.
     */
    public static Coupon create(
            String title,
            String code,
            String description,
            CouponMethod method,
            CouponDiscountType discountType,
            CouponValueType valueType,
            BigDecimal value,
            CouponAppliesTo appliesTo,
            LocalDate startDate,
            LocalTime startTime,
            UserId createdBy,
            Instant now
    ) {
        validateBasicInfo(method, code, value);

        return Coupon.builder()
                .id(CouponId.generate())
                .title(title)
                .code(code)
                .description(description)
                .method(method)
                .discountType(discountType)
                .valueType(valueType)
                .value(value)
                .appliesTo(appliesTo)
                .selectedItems("[]")
                .customerEligibility(CouponCustomerEligibility.EVERYONE)
                .minimumAmount(null)
                .minimumQuantity(null)
                .limitTotalUses(false)
                .maxTotalUses(null)
                .limitPerCustomer(false)
                .maxUsesPerCustomer(null)
                .combineWithProductDiscounts(false)
                .combineWithOrderDiscounts(false)
                .combineWithShippingDiscounts(false)
                .startDate(startDate)
                .startTime(startTime)
                .setEndDate(false)
                .endDate(null)
                .endTime(null)
                .status(CouponStatus.ACTIVE)
                .usedCount(0)
                .deletedAt(null)
                .triggerType(CouponTriggerType.NONE)
                .triggerProductId(null)
                .triggerProductName(null)
                .triggerQuantity(null)
                .appliesToFutureOrders(false)
                .specificCustomers("[]")
                .ruleConfig("{}")
                .createdBy(createdBy)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Reconfigure this coupon using the admin-facing contract.
     */
    public Coupon reconfigure(
            String title,
            String code,
            String description,
            CouponMethod method,
            CouponDiscountType discountType,
            CouponValueType valueType,
            BigDecimal value,
            CouponAppliesTo appliesTo,
            String selectedItems,
            CouponCustomerEligibility customerEligibility,
            Money minimumAmount,
            Integer minimumQuantity,
            boolean limitTotalUses,
            Integer maxTotalUses,
            boolean limitPerCustomer,
            Integer maxUsesPerCustomer,
            boolean combineWithProductDiscounts,
            boolean combineWithOrderDiscounts,
            boolean combineWithShippingDiscounts,
            LocalDate startDate,
            LocalTime startTime,
            boolean setEndDate,
            LocalDate endDate,
            LocalTime endTime,
            CouponStatus status,
            CouponTriggerType triggerType,
            String triggerProductId,
            String triggerProductName,
            boolean appliesToFutureOrders,
            String specificCustomers,
            String ruleConfig,
            Instant now
    ) {
        validateBasicInfo(method, code, value);
        validateTitle(method, title);
        validateUsageLimits(limitTotalUses, maxTotalUses, limitPerCustomer, maxUsesPerCustomer);
        validateSchedule(startDate, startTime, setEndDate, endDate, endTime);

        return this.toBuilder()
                .title(title)
                .code(code)
                .description(description)
                .method(method)
                .discountType(discountType)
                .valueType(valueType)
                .value(value)
                .appliesTo(appliesTo)
                .selectedItems(selectedItems != null ? selectedItems : "[]")
                .customerEligibility(customerEligibility != null ? customerEligibility : CouponCustomerEligibility.EVERYONE)
                .minimumAmount(minimumAmount)
                .minimumQuantity(minimumQuantity)
                .limitTotalUses(limitTotalUses)
                .maxTotalUses(limitTotalUses ? maxTotalUses : null)
                .limitPerCustomer(limitPerCustomer)
                .maxUsesPerCustomer(limitPerCustomer ? maxUsesPerCustomer : null)
                .combineWithProductDiscounts(combineWithProductDiscounts)
                .combineWithOrderDiscounts(combineWithOrderDiscounts)
                .combineWithShippingDiscounts(combineWithShippingDiscounts)
                .startDate(startDate)
                .startTime(startTime != null ? startTime : LocalTime.MIDNIGHT)
                .setEndDate(setEndDate)
                .endDate(setEndDate ? endDate : null)
                .endTime(setEndDate ? endTime : null)
                .status(status != null ? status : CouponStatus.ACTIVE)
                .triggerType(triggerType != null ? triggerType : CouponTriggerType.NONE)
                .triggerProductId(triggerProductId)
                .triggerProductName(triggerProductName)
                .appliesToFutureOrders(appliesToFutureOrders)
                .specificCustomers(specificCustomers != null ? specificCustomers : "[]")
                .ruleConfig(ruleConfig != null ? ruleConfig : "{}")
                .updatedAt(now)
                .build();
    }

    /**
     * Pause this coupon.
     */
    public Coupon pause(Instant now) {
        return this.toBuilder()
                .status(CouponStatus.PAUSED)
                .updatedAt(now)
                .build();
    }

    /**
     * Activate this coupon.
     */
    public Coupon activate(Instant now) {
        return this.toBuilder()
                .status(CouponStatus.ACTIVE)
                .updatedAt(now)
                .build();
    }

    /**
     * Mark as expired.
     */
    public Coupon expire(Instant now) {
        return this.toBuilder()
                .status(CouponStatus.EXPIRED)
                .updatedAt(now)
                .build();
    }

    /**
     * Soft delete coupon.
     */
    public Coupon softDelete(Instant now) {
        return this.toBuilder()
                .deletedAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Set end date.
     */
    public Coupon setEndDate(LocalDate endDate, LocalTime endTime, Instant now) {
        if (endDate.isBefore(startDate)) {
            throw new ValidationException("End date cannot be before start date");
        }

        return this.toBuilder()
                .setEndDate(true)
                .endDate(endDate)
                .endTime(endTime)
                .updatedAt(now)
                .build();
    }

    /**
     * Set usage limits.
     */
    public Coupon setUsageLimits(
            boolean limitTotalUses,
            Integer maxTotalUses,
            boolean limitPerCustomer,
            Integer maxUsesPerCustomer,
            Instant now
    ) {
        if (limitTotalUses && (maxTotalUses == null || maxTotalUses <= 0)) {
            throw new ValidationException("Max total uses must be positive when limit is enabled");
        }
        if (limitPerCustomer && (maxUsesPerCustomer == null || maxUsesPerCustomer <= 0)) {
            throw new ValidationException("Max uses per customer must be positive when limit is enabled");
        }

        return this.toBuilder()
                .limitTotalUses(limitTotalUses)
                .maxTotalUses(maxTotalUses)
                .limitPerCustomer(limitPerCustomer)
                .maxUsesPerCustomer(maxUsesPerCustomer)
                .updatedAt(now)
                .build();
    }

    /**
     * Set minimum requirements.
     */
    public Coupon setMinimumRequirements(Money minimumAmount, Integer minimumQuantity, Instant now) {
        return this.toBuilder()
                .minimumAmount(minimumAmount)
                .minimumQuantity(minimumQuantity)
                .updatedAt(now)
                .build();
    }

    /**
     * Update basic coupon information.
     */
    public Coupon updateBasicInfo(
            String title,
            String description,
            BigDecimal value,
            Instant now
    ) {
        if (value != null && value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Coupon value must be positive");
        }

        return this.toBuilder()
                .title(title != null ? title : this.title)
                .description(description != null ? description : this.description)
                .value(value != null ? value : this.value)
                .updatedAt(now)
                .build();
    }

    /**
     * Set combinability rules.
     */
    public Coupon setCombinability(
            boolean withProduct,
            boolean withOrder,
            boolean withShipping,
            Instant now
    ) {
        return this.toBuilder()
                .combineWithProductDiscounts(withProduct)
                .combineWithOrderDiscounts(withOrder)
                .combineWithShippingDiscounts(withShipping)
                .updatedAt(now)
                .build();
    }

    /**
     * Calculate discount amount for a given total.
     */
    public Money calculateDiscount(Money amount) {
        if (valueType == CouponValueType.PERCENTAGE) {
            return amount.percentage(value);
        } else {
            Money fixedDiscount = Money.of(value);
            // Don't discount more than the amount
            return amount.isLessThan(fixedDiscount) ? amount : fixedDiscount;
        }
    }

    /**
     * Check if coupon is active.
     */
    public boolean isActive() {
        return status == CouponStatus.ACTIVE && deletedAt == null;
    }

    /**
     * Check if coupon is automatic.
     */
    public boolean isAutomatic() {
        return method == CouponMethod.AUTOMATIC;
    }

    /**
     * Check if coupon requires a code.
     */
    public boolean requiresCode() {
        return method == CouponMethod.CODE;
    }

    /**
     * Check if coupon is for specific customers.
     */
    public boolean isForSpecificCustomers() {
        return customerEligibility == CouponCustomerEligibility.SPECIFIC_CUSTOMERS;
    }

    /**
     * Check if coupon has usage limit.
     */
    public boolean hasUsageLimit() {
        return limitTotalUses || limitPerCustomer;
    }

    /**
     * Check if coupon is combinable with other product discounts.
     */
    public boolean canCombineWithProductDiscounts() {
        return combineWithProductDiscounts;
    }

    /**
     * Check if coupon is combinable with other order discounts.
     */
    public boolean canCombineWithOrderDiscounts() {
        return combineWithOrderDiscounts;
    }

    /**
     * Check if coupon is combinable with shipping discounts.
     */
    public boolean canCombineWithShippingDiscounts() {
        return combineWithShippingDiscounts;
    }

    private static void validateBasicInfo(CouponMethod method, String code, BigDecimal value) {
        if (method == CouponMethod.CODE && (code == null || code.trim().isEmpty())) {
            throw new ValidationException("Code is required for code-based coupons");
        }
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Coupon value must be positive");
        }
    }

    private static void validateTitle(CouponMethod method, String title) {
        if (method == CouponMethod.AUTOMATIC && (title == null || title.trim().isEmpty())) {
            throw new ValidationException("Title is required for automatic coupons");
        }
    }

    private static void validateUsageLimits(
            boolean limitTotalUses,
            Integer maxTotalUses,
            boolean limitPerCustomer,
            Integer maxUsesPerCustomer
    ) {
        if (limitTotalUses && (maxTotalUses == null || maxTotalUses <= 0)) {
            throw new ValidationException("Max total uses must be positive when limit is enabled");
        }
        if (limitPerCustomer && (maxUsesPerCustomer == null || maxUsesPerCustomer <= 0)) {
            throw new ValidationException("Max uses per customer must be positive when limit is enabled");
        }
    }

    private static void validateSchedule(
            LocalDate startDate,
            LocalTime startTime,
            boolean setEndDate,
            LocalDate endDate,
            LocalTime endTime
    ) {
        if (startDate == null) {
            throw new ValidationException("Start date is required");
        }

        if (!setEndDate) {
            return;
        }

        if (endDate == null) {
            throw new ValidationException("End date is required when end date is enabled");
        }

        LocalDateTime start = LocalDateTime.of(startDate, startTime != null ? startTime : LocalTime.MIDNIGHT);
        LocalDateTime end = LocalDateTime.of(endDate, endTime != null ? endTime : LocalTime.MAX);
        if (end.isBefore(start)) {
            throw new ValidationException("End date cannot be before start date");
        }
    }
}

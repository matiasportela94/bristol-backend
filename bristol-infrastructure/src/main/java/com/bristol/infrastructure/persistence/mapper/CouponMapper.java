package com.bristol.infrastructure.persistence.mapper;

import com.bristol.domain.coupon.Coupon;
import com.bristol.domain.coupon.CouponAppliesTo;
import com.bristol.domain.coupon.CouponCustomerEligibility;
import com.bristol.domain.coupon.CouponDiscountType;
import com.bristol.domain.coupon.CouponId;
import com.bristol.domain.coupon.CouponMethod;
import com.bristol.domain.coupon.CouponStatus;
import com.bristol.domain.coupon.CouponTriggerType;
import com.bristol.domain.coupon.CouponValueType;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.infrastructure.persistence.entity.CouponEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Mapper for Coupon domain object and CouponEntity.
 */
@Component("couponPersistenceMapper")
public class CouponMapper {

    public Coupon toDomain(CouponEntity entity) {
        if (entity == null) {
            return null;
        }

        return Coupon.builder()
                .id(toCouponId(entity.getId()))
                .title(entity.getName())
                .code(entity.getCode())
                .description(entity.getDescription())
                .appliesTo(toDomainAppliesTo(entity.getAppliesTo()))
                .selectedItems(entity.getSelectedItems())
                .method(toDomainMethod(entity.getMethod()))
                .discountType(toDomainDiscountType(entity.getDiscountType()))
                .valueType(toDomainValueType(entity.getValueType()))
                .value(entity.getValue())
                .customerEligibility(toDomainCustomerEligibility(entity.getIsCustomerSpecific()))
                .minimumAmount(toMoney(entity.getMinimumPurchaseAmount()))
                .minimumQuantity(entity.getMinimumItemQuantity())
                .limitTotalUses(entity.getUsageLimitTotal() != null)
                .maxTotalUses(entity.getUsageLimitTotal())
                .limitPerCustomer(entity.getUsageLimitPerCustomer() != null)
                .maxUsesPerCustomer(entity.getUsageLimitPerCustomer())
                .combineWithProductDiscounts(Boolean.TRUE.equals(entity.getCombineWithProductDiscounts()))
                .combineWithOrderDiscounts(Boolean.TRUE.equals(entity.getCombineWithOrderDiscounts()))
                .combineWithShippingDiscounts(Boolean.TRUE.equals(entity.getCombineWithShippingDiscounts()))
                .startDate(toLocalDate(entity.getStartDate()))
                .startTime(toLocalTime(entity.getStartDate()))
                .setEndDate(entity.getEndDate() != null)
                .endDate(toLocalDate(entity.getEndDate()))
                .endTime(toLocalTime(entity.getEndDate()))
                .status(toDomainStatus(entity.getStatus()))
                .usedCount(entity.getTimesUsed())
                .deletedAt(entity.getDeletedAt())
                .triggerType(toDomainTriggerType(entity.getTriggerType()))
                .triggerProductId(entity.getTriggerProductId())
                .triggerProductName(entity.getTriggerProductName())
                .triggerQuantity(null)
                .appliesToFutureOrders(Boolean.TRUE.equals(entity.getAppliesToFutureOrders()))
                .specificCustomers(entity.getSpecificCustomers())
                .ruleConfig(entity.getRuleConfig())
                .createdBy(null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public CouponEntity toEntity(Coupon domain) {
        if (domain == null) {
            return null;
        }

        return CouponEntity.builder()
                .id(toUUID(domain.getId()))
                .code(domain.getCode())
                .name(domain.getTitle())
                .description(domain.getDescription())
                .appliesTo(toEntityAppliesTo(domain.getAppliesTo()))
                .selectedItems(domain.getSelectedItems())
                .method(toEntityMethod(domain.getMethod()))
                .discountType(toEntityDiscountType(domain.getDiscountType()))
                .valueType(toEntityValueType(domain.getValueType()))
                .value(domain.getValue())
                .scheduleType(resolveScheduleType(domain))
                .startDate(toLocalDateTime(domain.getStartDate(), domain.getStartTime()))
                .endDate(toLocalDateTime(domain.getEndDate(), domain.getEndTime()))
                .status(toEntityStatus(domain.getStatus()))
                .minimumRequirementType(resolveMinimumRequirementType(domain))
                .minimumPurchaseAmount(toBigDecimal(domain.getMinimumAmount()))
                .minimumItemQuantity(domain.getMinimumQuantity())
                .usageLimitTotal(domain.getMaxTotalUses())
                .usageLimitPerCustomer(domain.getMaxUsesPerCustomer())
                .timesUsed(domain.getUsedCount())
                .isCustomerSpecific(domain.getCustomerEligibility() == CouponCustomerEligibility.SPECIFIC_CUSTOMERS)
                .applicableProductCategory(CouponEntity.ApplicableProductCategoryEnum.ALL)
                .applicableProductSubcategory(CouponEntity.ApplicableProductSubcategoryEnum.ALL)
                .combineWithProductDiscounts(domain.isCombineWithProductDiscounts())
                .combineWithOrderDiscounts(domain.isCombineWithOrderDiscounts())
                .combineWithShippingDiscounts(domain.isCombineWithShippingDiscounts())
                .triggerType(toEntityTriggerType(domain.getTriggerType()))
                .triggerProductId(domain.getTriggerProductId())
                .triggerProductName(domain.getTriggerProductName())
                .appliesToFutureOrders(domain.isAppliesToFutureOrders())
                .specificCustomers(domain.getSpecificCustomers())
                .ruleConfig(domain.getRuleConfig())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .deletedAt(domain.getDeletedAt())
                .build();
    }

    private CouponId toCouponId(UUID uuid) {
        return uuid != null ? new CouponId(uuid) : null;
    }

    private UUID toUUID(CouponId id) {
        return id != null ? id.getValue() : null;
    }

    private Money toMoney(BigDecimal amount) {
        return amount != null ? Money.of(amount) : null;
    }

    private BigDecimal toBigDecimal(Money money) {
        return money != null ? money.getAmount() : null;
    }

    private LocalDate toLocalDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalDate() : null;
    }

    private LocalTime toLocalTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalTime() : null;
    }

    private LocalDateTime toLocalDateTime(LocalDate date, LocalTime time) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.of(date, time != null ? time : LocalTime.MIDNIGHT);
    }

    private CouponMethod toDomainMethod(CouponEntity.CouponMethodEnum method) {
        return method != null ? CouponMethod.valueOf(method.name()) : null;
    }

    private CouponAppliesTo toDomainAppliesTo(CouponEntity.CouponAppliesToEnum appliesTo) {
        return appliesTo != null ? CouponAppliesTo.valueOf(appliesTo.name()) : CouponAppliesTo.ENTIRE_ORDER;
    }

    private CouponCustomerEligibility toDomainCustomerEligibility(Boolean isCustomerSpecific) {
        return Boolean.TRUE.equals(isCustomerSpecific)
                ? CouponCustomerEligibility.SPECIFIC_CUSTOMERS
                : CouponCustomerEligibility.EVERYONE;
    }

    private CouponEntity.CouponMethodEnum toEntityMethod(CouponMethod method) {
        return method != null ? CouponEntity.CouponMethodEnum.valueOf(method.name()) : null;
    }

    private CouponEntity.CouponAppliesToEnum toEntityAppliesTo(CouponAppliesTo appliesTo) {
        return appliesTo != null ? CouponEntity.CouponAppliesToEnum.valueOf(appliesTo.name()) : CouponEntity.CouponAppliesToEnum.ENTIRE_ORDER;
    }

    private CouponDiscountType toDomainDiscountType(CouponEntity.CouponDiscountTypeEnum discountType) {
        return discountType != null ? CouponDiscountType.valueOf(discountType.name()) : null;
    }

    private CouponEntity.CouponDiscountTypeEnum toEntityDiscountType(CouponDiscountType discountType) {
        return discountType != null ? CouponEntity.CouponDiscountTypeEnum.valueOf(discountType.name()) : null;
    }

    private CouponValueType toDomainValueType(CouponEntity.CouponValueTypeEnum valueType) {
        return valueType != null ? CouponValueType.valueOf(valueType.name()) : null;
    }

    private CouponEntity.CouponValueTypeEnum toEntityValueType(CouponValueType valueType) {
        return valueType != null ? CouponEntity.CouponValueTypeEnum.valueOf(valueType.name()) : null;
    }

    private CouponTriggerType toDomainTriggerType(CouponEntity.CouponTriggerTypeEnum triggerType) {
        return triggerType != null ? CouponTriggerType.valueOf(triggerType.name()) : CouponTriggerType.NONE;
    }

    private CouponEntity.CouponTriggerTypeEnum toEntityTriggerType(CouponTriggerType triggerType) {
        return triggerType != null ? CouponEntity.CouponTriggerTypeEnum.valueOf(triggerType.name()) : CouponEntity.CouponTriggerTypeEnum.NONE;
    }

    private CouponStatus toDomainStatus(CouponEntity.CouponStatusEnum status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case ACTIVE -> CouponStatus.ACTIVE;
            case INACTIVE -> CouponStatus.PAUSED;
            case EXPIRED -> CouponStatus.EXPIRED;
        };
    }

    private CouponEntity.CouponStatusEnum toEntityStatus(CouponStatus status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case ACTIVE -> CouponEntity.CouponStatusEnum.ACTIVE;
            case PAUSED -> CouponEntity.CouponStatusEnum.INACTIVE;
            case EXPIRED -> CouponEntity.CouponStatusEnum.EXPIRED;
        };
    }

    private CouponEntity.CouponScheduleTypeEnum resolveScheduleType(Coupon domain) {
        if (domain.getStartDate() == null && domain.getEndDate() == null) {
            return CouponEntity.CouponScheduleTypeEnum.ALWAYS;
        }
        return CouponEntity.CouponScheduleTypeEnum.SCHEDULED;
    }

    private CouponEntity.MinimumRequirementTypeEnum resolveMinimumRequirementType(Coupon domain) {
        boolean hasAmount = domain.getMinimumAmount() != null;
        boolean hasQuantity = domain.getMinimumQuantity() != null;

        if (hasAmount && hasQuantity) {
            return CouponEntity.MinimumRequirementTypeEnum.BOTH;
        }
        if (hasAmount) {
            return CouponEntity.MinimumRequirementTypeEnum.PURCHASE_AMOUNT;
        }
        if (hasQuantity) {
            return CouponEntity.MinimumRequirementTypeEnum.ITEM_QUANTITY;
        }
        return CouponEntity.MinimumRequirementTypeEnum.NONE;
    }
}

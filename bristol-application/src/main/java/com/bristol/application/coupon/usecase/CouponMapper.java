package com.bristol.application.coupon.usecase;

import com.bristol.application.coupon.dto.CouponDto;
import com.bristol.application.coupon.dto.CouponBenefitPayload;
import com.bristol.application.coupon.dto.CouponScopePayload;
import com.bristol.domain.coupon.Coupon;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert between Coupon domain entity and CouponDto.
 */
@Component
public class CouponMapper {

    public CouponDto toDto(Coupon coupon) {
        return CouponDto.builder()
                .id(coupon.getId().getValue().toString())
                .title(coupon.getTitle())
                .code(coupon.getCode())
                .description(coupon.getDescription())
                .method(coupon.getMethod())
                .discountType(coupon.getDiscountType())
                .valueType(coupon.getValueType())
                .value(coupon.getValue())
                .appliesTo(coupon.getAppliesTo())
                .selectedItems(coupon.getSelectedItems())
                .customerEligibility(coupon.getCustomerEligibility())
                .minAmount(coupon.getMinimumAmount() != null ? coupon.getMinimumAmount().getAmount() : null)
                .minQuantity(coupon.getMinimumQuantity())
                .limitTotalUses(coupon.isLimitTotalUses())
                .maxTotalUses(coupon.getMaxTotalUses())
                .limitPerCustomer(coupon.isLimitPerCustomer())
                .maxUsesPerCustomer(coupon.getMaxUsesPerCustomer())
                .combineWithProduct(coupon.isCombineWithProductDiscounts())
                .combineWithOrder(coupon.isCombineWithOrderDiscounts())
                .combineWithShipping(coupon.isCombineWithShippingDiscounts())
                .startDate(coupon.getStartDate())
                .startTime(coupon.getStartTime())
                .setEndDate(coupon.isSetEndDate())
                .endDate(coupon.getEndDate())
                .endTime(coupon.getEndTime())
                .status(coupon.getStatus())
                .priority(coupon.getPriority())
                .usedCount(coupon.getUsedCount())
                .triggerType(coupon.getTriggerType())
                .triggerProductId(coupon.getTriggerProductId())
                .triggerProductName(coupon.getTriggerProductName())
                .appliesToFutureOrders(coupon.isAppliesToFutureOrders())
                .specificCustomers(coupon.getSpecificCustomers())
                .ruleConfig(coupon.getRuleConfig())
                .scope(toScopePayload(coupon))
                .benefit(toBenefitPayload(coupon))
                .createdAt(coupon.getCreatedAt())
                .updatedAt(coupon.getUpdatedAt())
                .build();
    }

    private CouponScopePayload toScopePayload(Coupon coupon) {
        return CouponScopePayload.builder()
                .type(coupon.getScope().getType())
                .productIds(coupon.getScope().getProductIds())
                .variantIds(coupon.getScope().getVariantIds())
                .categories(coupon.getScope().getCategories())
                .subcategories(coupon.getScope().getSubcategories())
                .beerTypes(coupon.getScope().getBeerTypes())
                .build();
    }

    private CouponBenefitPayload toBenefitPayload(Coupon coupon) {
        return CouponBenefitPayload.builder()
                .type(coupon.getBenefit().getType())
                .buyQuantity(coupon.getBenefit().getBuyQuantity())
                .freeQuantity(coupon.getBenefit().getFreeQuantity())
                .payQuantity(coupon.getBenefit().getPayQuantity())
                .thresholdQuantity(coupon.getBenefit().getThresholdQuantity())
                .triggerQuantity(coupon.getBenefit().getTriggerQuantity())
                .advanced(coupon.getBenefit().isAdvancedBenefit())
                .build();
    }
}

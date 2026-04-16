package com.bristol.application.coupon.usecase;

import com.bristol.application.coupon.dto.CreateCouponRequest;
import com.bristol.application.coupon.dto.CouponDto;
import com.bristol.domain.coupon.Coupon;
import com.bristol.domain.coupon.CouponCustomerEligibility;
import com.bristol.domain.coupon.CouponRepository;
import com.bristol.domain.coupon.CouponStatus;
import com.bristol.domain.coupon.CouponTriggerType;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;

/**
 * Use case to create a new coupon.
 */
@Service
@RequiredArgsConstructor
public class CreateCouponUseCase {

    private final CouponRepository couponRepository;
    private final CouponMapper couponMapper;
    private final CouponAdminPayloadResolver couponAdminPayloadResolver;
    private final CouponDefinitionValidator couponDefinitionValidator;
    private final TimeProvider timeProvider;

    @Transactional
    public CouponDto execute(CreateCouponRequest request) {
        Instant now = timeProvider.now();
        LocalTime startTime = request.getStartTime() != null ? request.getStartTime() : LocalTime.MIDNIGHT;
        UserId createdBy = request.getCreatedBy() != null ? new UserId(request.getCreatedBy()) : null;
        String normalizedCode = normalizeCode(request.getCode());
        String normalizedTitle = normalizeTitle(request.getTitle(), normalizedCode);
        var resolvedAppliesTo = couponAdminPayloadResolver.resolveAppliesTo(request.getAppliesTo(), request.getScope());
        var resolvedTriggerType = couponAdminPayloadResolver.resolveTriggerType(request.getTriggerType(), request.getBenefit());
        String resolvedSelectedItems = couponAdminPayloadResolver.resolveSelectedItems(request.getSelectedItems(), request.getScope());
        String resolvedRuleConfig = couponAdminPayloadResolver.resolveRuleConfig(request.getRuleConfig(), request.getBenefit());
        couponDefinitionValidator.validate(
                request.getDiscountType(),
                request.getValueType(),
                request.getValue(),
                resolvedAppliesTo,
                resolvedSelectedItems,
                resolvedTriggerType,
                resolvedRuleConfig
        );

        Coupon coupon = Coupon.create(
                normalizedTitle,
                normalizedCode,
                request.getDescription(),
                request.getMethod(),
                request.getDiscountType(),
                request.getValueType(),
                request.getValue(),
                resolvedAppliesTo,
                request.getStartDate(),
                startTime,
                createdBy,
                now
        ).reconfigure(
                normalizedTitle,
                normalizedCode,
                request.getDescription(),
                request.getMethod(),
                request.getDiscountType(),
                request.getValueType(),
                request.getValue(),
                resolvedAppliesTo,
                resolvedSelectedItems,
                request.getCustomerEligibility() != null ? request.getCustomerEligibility() : CouponCustomerEligibility.EVERYONE,
                toMoney(request.getMinAmount()),
                request.getMinQuantity(),
                request.isLimitTotalUses(),
                request.getMaxTotalUses(),
                request.isLimitPerCustomer(),
                resolveMaxUsesPerCustomer(request.isLimitPerCustomer(), request.getMaxUsesPerCustomer()),
                request.isCombineWithProduct(),
                request.isCombineWithOrder(),
                request.isCombineWithShipping(),
                request.getStartDate(),
                startTime,
                request.isSetEndDate(),
                request.getEndDate(),
                request.getEndTime(),
                resolveStatus(request.getStatus()),
                request.getPriority(),
                resolvedTriggerType,
                request.getTriggerProductId(),
                request.getTriggerProductName(),
                request.isAppliesToFutureOrders(),
                normalizeJsonPayload(request.getSpecificCustomers()),
                resolvedRuleConfig,
                now
        );

        Coupon savedCoupon = couponRepository.save(coupon);
        return couponMapper.toDto(savedCoupon);
    }

    private String normalizeTitle(String title, String code) {
        if (title != null && !title.isBlank()) {
            return title.trim();
        }
        return code;
    }

    private String normalizeCode(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        return code.trim().toUpperCase();
    }

    private Integer resolveMaxUsesPerCustomer(boolean limitPerCustomer, Integer requestedValue) {
        if (!limitPerCustomer) {
            return null;
        }
        return requestedValue != null ? requestedValue : 1;
    }

    private CouponStatus resolveStatus(CouponStatus requestedStatus) {
        if (requestedStatus == null) {
            return CouponStatus.ACTIVE;
        }
        return requestedStatus == CouponStatus.DRAFT
                || requestedStatus == CouponStatus.PAUSED
                || requestedStatus == CouponStatus.EXPIRED
                ? requestedStatus
                : CouponStatus.ACTIVE;
    }

    private Money toMoney(BigDecimal amount) {
        return amount != null ? Money.of(amount) : null;
    }

    private String normalizeJsonPayload(String value) {
        return value != null && !value.isBlank() ? value : "[]";
    }
}

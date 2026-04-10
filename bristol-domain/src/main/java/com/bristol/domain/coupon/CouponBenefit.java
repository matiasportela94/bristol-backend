package com.bristol.domain.coupon;

import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.valueobject.Money;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Internal normalized view of coupon discount behavior.
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponBenefit {

    private static final Pattern INTEGER_FIELD_PATTERN = Pattern.compile("\"([^\"]+)\"\\s*:\\s*(\\d+)");

    private final CouponBenefitType type;
    private final CouponDiscountType discountType;
    private final CouponValueType valueType;
    private final BigDecimal value;
    private final CouponTriggerType triggerType;
    private final String rawRuleConfig;

    public static CouponBenefit from(
            CouponDiscountType discountType,
            CouponValueType valueType,
            BigDecimal value,
            CouponTriggerType triggerType,
            String ruleConfig
    ) {
        CouponTriggerType resolvedTriggerType = triggerType != null ? triggerType : CouponTriggerType.NONE;
        String normalizedRuleConfig = normalizePayload(ruleConfig, "{}");
        CouponBenefitType benefitType = resolveType(discountType, valueType, resolvedTriggerType, normalizedRuleConfig);

        return new CouponBenefit(
                benefitType,
                discountType,
                valueType,
                value,
                resolvedTriggerType,
                normalizedRuleConfig
        );
    }

    public Money calculateDiscount(Money amount) {
        if (!supportsDirectDiscountCalculation()) {
            throw new ValidationException("Benefit type " + type + " requires PromotionEngine evaluation");
        }

        if (valueType == CouponValueType.PERCENTAGE) {
            return amount.percentage(value);
        }

        Money fixedDiscount = Money.of(value);
        return amount.isLessThan(fixedDiscount) ? amount : fixedDiscount;
    }

    public boolean supportsDirectDiscountCalculation() {
        return switch (type) {
            case ORDER_PERCENTAGE,
                 ORDER_FIXED_AMOUNT,
                 PRODUCT_PERCENTAGE,
                 PRODUCT_FIXED_AMOUNT,
                 SHIPPING_PERCENTAGE,
                 SHIPPING_FIXED_AMOUNT -> true;
            default -> false;
        };
    }

    public boolean isAdvancedBenefit() {
        return !supportsDirectDiscountCalculation();
    }

    public Integer getBuyQuantity() {
        return switch (type) {
            case BUY_X_GET_Y -> extractPositiveInteger("buyX", "buyQuantity", "requiredQuantity");
            case BUY_X_FOR_Y -> extractPositiveInteger("buyX", "buyQuantity", "requiredQuantity", "quantity");
            default -> null;
        };
    }

    public Integer getFreeQuantity() {
        return type == CouponBenefitType.BUY_X_GET_Y
                ? extractPositiveInteger("getY", "freeQuantity", "discountQuantity")
                : null;
    }

    public Integer getPayQuantity() {
        return type == CouponBenefitType.BUY_X_FOR_Y
                ? extractPositiveInteger("forY", "payY", "payQuantity", "chargedQuantity")
                : null;
    }

    public Integer getThresholdQuantity() {
        return switch (type) {
            case PERCENTAGE_ON_QUANTITY, QUANTITY_RULE ->
                    extractPositiveInteger("minQuantity", "minimumQuantity", "thresholdQuantity", "quantity", "buyQuantity");
            default -> null;
        };
    }

    public Integer getTriggerQuantity() {
        return type == CouponBenefitType.TRIGGERED_PRODUCT_DISCOUNT
                ? extractPositiveInteger("triggerQuantity", "requiredTriggerQuantity", "minimumTriggerQuantity")
                : null;
    }

    private static CouponBenefitType resolveType(
            CouponDiscountType discountType,
            CouponValueType valueType,
            CouponTriggerType triggerType,
            String ruleConfig
    ) {
        if (triggerType == CouponTriggerType.BUY_X_GET_Y) {
            return CouponBenefitType.BUY_X_GET_Y;
        }

        if (containsAnyIgnoreCase(ruleConfig, "buy_x_for_y", "buyXForY")) {
            return CouponBenefitType.BUY_X_FOR_Y;
        }

        if (containsAnyIgnoreCase(ruleConfig, "percentage_on_quantity", "percentageOnQuantity")) {
            return CouponBenefitType.PERCENTAGE_ON_QUANTITY;
        }

        if (triggerType == CouponTriggerType.PRODUCT_PURCHASE) {
            return CouponBenefitType.TRIGGERED_PRODUCT_DISCOUNT;
        }

        if (!"{}".equals(ruleConfig)) {
            return CouponBenefitType.QUANTITY_RULE;
        }

        if (discountType == CouponDiscountType.SHIPPING) {
            return valueType == CouponValueType.PERCENTAGE
                    ? CouponBenefitType.SHIPPING_PERCENTAGE
                    : CouponBenefitType.SHIPPING_FIXED_AMOUNT;
        }

        if (discountType == CouponDiscountType.PRODUCT) {
            return valueType == CouponValueType.PERCENTAGE
                    ? CouponBenefitType.PRODUCT_PERCENTAGE
                    : CouponBenefitType.PRODUCT_FIXED_AMOUNT;
        }

        return valueType == CouponValueType.PERCENTAGE
                ? CouponBenefitType.ORDER_PERCENTAGE
                : CouponBenefitType.ORDER_FIXED_AMOUNT;
    }

    private static String normalizePayload(String payload, String fallback) {
        return payload != null && !payload.isBlank() ? payload : fallback;
    }

    private static boolean containsAnyIgnoreCase(String value, String... candidates) {
        String normalized = value.toLowerCase();
        for (String candidate : candidates) {
            if (normalized.contains(candidate.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private Integer extractPositiveInteger(String... keys) {
        Matcher matcher = INTEGER_FIELD_PATTERN.matcher(rawRuleConfig);
        while (matcher.find()) {
            String key = matcher.group(1);
            for (String candidate : keys) {
                if (candidate.equalsIgnoreCase(key)) {
                    int value = Integer.parseInt(matcher.group(2));
                    return value > 0 ? value : null;
                }
            }
        }
        return null;
    }
}

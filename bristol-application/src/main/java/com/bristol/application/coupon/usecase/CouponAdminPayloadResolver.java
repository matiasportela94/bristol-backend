package com.bristol.application.coupon.usecase;

import com.bristol.application.coupon.dto.CouponBenefitPayload;
import com.bristol.application.coupon.dto.CouponScopePayload;
import com.bristol.domain.coupon.CouponAppliesTo;
import com.bristol.domain.coupon.CouponBenefitType;
import com.bristol.domain.coupon.CouponTriggerType;
import com.bristol.domain.shared.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Normalizes additive structured coupon admin payloads into persisted legacy fields.
 */
@Component
public class CouponAdminPayloadResolver {

    public CouponAppliesTo resolveAppliesTo(CouponAppliesTo requestedAppliesTo, CouponScopePayload scopePayload) {
        if (scopePayload == null || scopePayload.getType() == null) {
            return requestedAppliesTo != null ? requestedAppliesTo : CouponAppliesTo.ENTIRE_ORDER;
        }

        return switch (scopePayload.getType()) {
            case ENTIRE_ORDER -> CouponAppliesTo.ENTIRE_ORDER;
            case COLLECTION -> CouponAppliesTo.COLLECTIONS;
            case SPECIFIC_PRODUCT, CATEGORY, SUBCATEGORY, BEER_TYPE, MANUAL_SELECTION ->
                    CouponAppliesTo.SPECIFIC_PRODUCTS;
        };
    }

    public String resolveSelectedItems(String rawSelectedItems, CouponScopePayload scopePayload) {
        if (scopePayload == null || scopePayload.getType() == null) {
            return normalizeJsonArray(rawSelectedItems);
        }

        validateScopePayload(scopePayload);

        return switch (scopePayload.getType()) {
            case ENTIRE_ORDER, COLLECTION -> "[]";
            case SPECIFIC_PRODUCT, MANUAL_SELECTION -> buildProductSelection(scopePayload);
            case CATEGORY -> buildEnumSelection("category", scopePayload.getCategories());
            case SUBCATEGORY -> buildEnumSelection("subcategory", scopePayload.getSubcategories());
            case BEER_TYPE -> buildEnumSelection("beerType", scopePayload.getBeerTypes());
        };
    }

    public CouponTriggerType resolveTriggerType(CouponTriggerType requestedTriggerType, CouponBenefitPayload benefitPayload) {
        if (benefitPayload == null || benefitPayload.getType() == null) {
            return requestedTriggerType != null ? requestedTriggerType : CouponTriggerType.NONE;
        }

        return switch (benefitPayload.getType()) {
            case BUY_X_GET_Y -> CouponTriggerType.BUY_X_GET_Y;
            case TRIGGERED_PRODUCT_DISCOUNT -> CouponTriggerType.PRODUCT_PURCHASE;
            default -> CouponTriggerType.NONE;
        };
    }

    public String resolveRuleConfig(String rawRuleConfig, CouponBenefitPayload benefitPayload) {
        if (benefitPayload == null || benefitPayload.getType() == null) {
            return normalizeRuleConfig(rawRuleConfig);
        }

        validateBenefitPayload(benefitPayload);

        return switch (benefitPayload.getType()) {
            case ORDER_PERCENTAGE,
                 ORDER_FIXED_AMOUNT,
                 PRODUCT_PERCENTAGE,
                 PRODUCT_FIXED_AMOUNT,
                 SHIPPING_PERCENTAGE,
                 SHIPPING_FIXED_AMOUNT -> "{}";
            case BUY_X_GET_Y -> "{\"buyX\":" + benefitPayload.getBuyQuantity()
                    + ",\"getY\":" + benefitPayload.getFreeQuantity() + "}";
            case BUY_X_FOR_Y -> "{\"type\":\"buy_x_for_y\",\"buyX\":" + benefitPayload.getBuyQuantity()
                    + ",\"forY\":" + benefitPayload.getPayQuantity() + "}";
            case PERCENTAGE_ON_QUANTITY -> "{\"type\":\"percentage_on_quantity\",\"minQuantity\":"
                    + benefitPayload.getThresholdQuantity() + "}";
            case QUANTITY_RULE -> "{\"type\":\"quantity_rule\",\"minQuantity\":"
                    + benefitPayload.getThresholdQuantity() + "}";
            case TRIGGERED_PRODUCT_DISCOUNT -> benefitPayload.getTriggerQuantity() != null
                    ? "{\"triggerQuantity\":" + benefitPayload.getTriggerQuantity() + "}"
                    : "{}";
        };
    }

    private void validateScopePayload(CouponScopePayload scopePayload) {
        switch (scopePayload.getType()) {
            case SPECIFIC_PRODUCT, MANUAL_SELECTION -> {
                if (isEmpty(scopePayload.getProductIds()) && isEmpty(scopePayload.getVariantIds())) {
                    throw new ValidationException("Product-scoped promotions require at least one product or variant");
                }
            }
            case CATEGORY -> requireValues(scopePayload.getCategories(), "Category-scoped promotions require at least one category");
            case SUBCATEGORY -> requireValues(scopePayload.getSubcategories(), "Subcategory-scoped promotions require at least one subcategory");
            case BEER_TYPE -> requireValues(scopePayload.getBeerTypes(), "Beer-type promotions require at least one beer type");
            default -> {
            }
        }
    }

    private void validateBenefitPayload(CouponBenefitPayload benefitPayload) {
        CouponBenefitType type = benefitPayload.getType();
        switch (type) {
            case BUY_X_GET_Y -> {
                requirePositive(benefitPayload.getBuyQuantity(), "Buy X Get Y promotions require a positive buy quantity");
                requirePositive(benefitPayload.getFreeQuantity(), "Buy X Get Y promotions require a positive free quantity");
            }
            case BUY_X_FOR_Y -> {
                requirePositive(benefitPayload.getBuyQuantity(), "Buy X For Y promotions require a positive buy quantity");
                requirePositive(benefitPayload.getPayQuantity(), "Buy X For Y promotions require a positive pay quantity");
                if (benefitPayload.getPayQuantity() >= benefitPayload.getBuyQuantity()) {
                    throw new ValidationException("Buy X For Y promotions require pay quantity to be lower than buy quantity");
                }
            }
            case PERCENTAGE_ON_QUANTITY, QUANTITY_RULE ->
                    requirePositive(benefitPayload.getThresholdQuantity(), "Quantity-based promotions require a positive threshold quantity");
            case TRIGGERED_PRODUCT_DISCOUNT -> {
                if (benefitPayload.getTriggerQuantity() != null && benefitPayload.getTriggerQuantity() <= 0) {
                    throw new ValidationException("Triggered product discounts require a positive trigger quantity when provided");
                }
            }
            default -> {
            }
        }
    }

    private String buildProductSelection(CouponScopePayload scopePayload) {
        List<String> entries = new ArrayList<>();

        for (String productId : normalizeStrings(scopePayload.getProductIds())) {
            entries.add("{\"productId\":\"" + escapeJson(productId) + "\"}");
        }
        for (String variantId : normalizeStrings(scopePayload.getVariantIds())) {
            entries.add("{\"variantId\":\"" + escapeJson(variantId) + "\"}");
        }

        return "[" + String.join(",", entries) + "]";
    }

    private String buildEnumSelection(String field, Collection<? extends Enum<?>> values) {
        List<String> entries = new ArrayList<>();
        for (Enum<?> value : values != null ? values : List.<Enum<?>>of()) {
            entries.add("{\"" + field + "\":\"" + value.name() + "\"}");
        }
        return "[" + String.join(",", entries) + "]";
    }

    private Set<String> normalizeStrings(Collection<String> values) {
        Set<String> normalized = new LinkedHashSet<>();
        if (values == null) {
            return normalized;
        }

        for (String value : values) {
            if (value != null && !value.isBlank()) {
                normalized.add(value.trim());
            }
        }
        return normalized;
    }

    private void requireValues(Collection<?> values, String message) {
        if (isEmpty(values)) {
            throw new ValidationException(message);
        }
    }

    private void requirePositive(Integer value, String message) {
        if (value == null || value <= 0) {
            throw new ValidationException(message);
        }
    }

    private boolean isEmpty(Collection<?> values) {
        return values == null || values.isEmpty();
    }

    private String normalizeJsonArray(String value) {
        return value != null && !value.isBlank() ? value : "[]";
    }

    private String normalizeRuleConfig(String value) {
        return value != null && !value.isBlank() ? value : "{}";
    }

    private String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}

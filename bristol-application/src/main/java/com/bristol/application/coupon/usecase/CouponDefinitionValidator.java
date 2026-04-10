package com.bristol.application.coupon.usecase;

import com.bristol.domain.coupon.CouponBenefit;
import com.bristol.domain.coupon.CouponBenefitType;
import com.bristol.domain.coupon.CouponDiscountType;
import com.bristol.domain.coupon.CouponScope;
import com.bristol.domain.coupon.CouponScopeType;
import com.bristol.domain.coupon.CouponTriggerType;
import com.bristol.domain.coupon.CouponValueType;
import com.bristol.domain.shared.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Validates that admin-defined coupons only use behaviors currently supported by the promotion engine.
 */
@Component
public class CouponDefinitionValidator {

    public void validate(
            CouponDiscountType discountType,
            CouponValueType valueType,
            BigDecimal value,
            com.bristol.domain.coupon.CouponAppliesTo appliesTo,
            String selectedItems,
            CouponTriggerType triggerType,
            String ruleConfig
    ) {
        CouponScope scope = CouponScope.from(appliesTo, selectedItems);
        CouponBenefit benefit = CouponBenefit.from(discountType, valueType, value, triggerType, ruleConfig);

        validateScope(discountType, scope);
        validateBenefit(discountType, benefit);
    }

    private void validateScope(CouponDiscountType discountType, CouponScope scope) {
        if ((discountType == CouponDiscountType.ORDER || discountType == CouponDiscountType.SHIPPING)
                && !scope.isEntireOrder()) {
            throw new ValidationException("This coupon scope is not yet supported for order repricing");
        }

        if (discountType == CouponDiscountType.PRODUCT) {
            CouponScopeType scopeType = scope.getType();
            if (scopeType != CouponScopeType.ENTIRE_ORDER
                    && scopeType != CouponScopeType.SPECIFIC_PRODUCT
                    && scopeType != CouponScopeType.MANUAL_SELECTION
                    && scopeType != CouponScopeType.CATEGORY
                    && scopeType != CouponScopeType.SUBCATEGORY
                    && scopeType != CouponScopeType.BEER_TYPE) {
                throw new ValidationException("This coupon scope is not yet supported for product repricing");
            }
        }
    }

    private void validateBenefit(CouponDiscountType discountType, CouponBenefit benefit) {
        if (benefit.supportsDirectDiscountCalculation()) {
            return;
        }

        if (discountType == CouponDiscountType.PRODUCT) {
            switch (benefit.getType()) {
                case TRIGGERED_PRODUCT_DISCOUNT, BUY_X_GET_Y, BUY_X_FOR_Y, PERCENTAGE_ON_QUANTITY, QUANTITY_RULE -> {
                    return;
                }
                default -> {
                }
            }
        }

        throw new ValidationException("This coupon benefit requires PromotionEngine advanced evaluation");
    }
}

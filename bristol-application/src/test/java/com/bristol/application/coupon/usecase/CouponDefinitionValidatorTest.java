package com.bristol.application.coupon.usecase;

import com.bristol.domain.coupon.CouponAppliesTo;
import com.bristol.domain.coupon.CouponDiscountType;
import com.bristol.domain.coupon.CouponTriggerType;
import com.bristol.domain.coupon.CouponValueType;
import com.bristol.domain.shared.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponDefinitionValidatorTest {

    private final CouponDefinitionValidator validator = new CouponDefinitionValidator();

    @Test
    void validateShouldRejectOrderCouponsWithProductScopedSelection() {
        assertThatThrownBy(() -> validator.validate(
                CouponDiscountType.ORDER,
                CouponValueType.PERCENTAGE,
                BigDecimal.TEN,
                CouponAppliesTo.SPECIFIC_PRODUCTS,
                "[{\"productId\":\"abc-123\"}]",
                CouponTriggerType.NONE,
                "{}"
        ))
                .isInstanceOf(ValidationException.class)
                .hasMessage("This coupon scope is not yet supported for order repricing");
    }

    @Test
    void validateShouldRejectCollectionScopedProductCoupons() {
        assertThatThrownBy(() -> validator.validate(
                CouponDiscountType.PRODUCT,
                CouponValueType.PERCENTAGE,
                BigDecimal.TEN,
                CouponAppliesTo.COLLECTIONS,
                "[]",
                CouponTriggerType.NONE,
                "{}"
        ))
                .isInstanceOf(ValidationException.class)
                .hasMessage("This coupon scope is not yet supported for product repricing");
    }

    @Test
    void validateShouldAllowSupportedAdvancedProductBenefit() {
        assertThatCode(() -> validator.validate(
                CouponDiscountType.PRODUCT,
                CouponValueType.PERCENTAGE,
                BigDecimal.TEN,
                CouponAppliesTo.SPECIFIC_PRODUCTS,
                "[{\"category\":\"MERCHANDISING\"}]",
                CouponTriggerType.NONE,
                "{\"type\":\"percentage_on_quantity\",\"minQuantity\":3}"
        )).doesNotThrowAnyException();
    }
}

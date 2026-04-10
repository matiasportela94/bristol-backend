package com.bristol.application.coupon.usecase;

import com.bristol.application.coupon.dto.CouponDto;
import com.bristol.domain.coupon.Coupon;
import com.bristol.domain.coupon.CouponAppliesTo;
import com.bristol.domain.coupon.CouponBenefitType;
import com.bristol.domain.coupon.CouponDiscountType;
import com.bristol.domain.coupon.CouponMethod;
import com.bristol.domain.coupon.CouponScopeType;
import com.bristol.domain.coupon.CouponStatus;
import com.bristol.domain.coupon.CouponTriggerType;
import com.bristol.domain.coupon.CouponValueType;
import com.bristol.domain.product.ProductCategory;
import com.bristol.domain.user.UserId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class CouponMapperTest {

    private final CouponMapper couponMapper = new CouponMapper();

    @Test
    void toDtoShouldExposeNormalizedScopeAndBenefitPayloads() {
        Instant now = Instant.parse("2026-04-07T12:00:00Z");
        Coupon coupon = Coupon.create(
                "Category promo",
                "CAT15",
                "Category promo",
                CouponMethod.AUTOMATIC,
                CouponDiscountType.PRODUCT,
                CouponValueType.PERCENTAGE,
                BigDecimal.valueOf(15),
                CouponAppliesTo.SPECIFIC_PRODUCTS,
                LocalDate.parse("2026-04-07"),
                LocalTime.MIDNIGHT,
                UserId.generate(),
                now
        ).reconfigure(
                "Category promo",
                "CAT15",
                "Category promo",
                CouponMethod.AUTOMATIC,
                CouponDiscountType.PRODUCT,
                CouponValueType.PERCENTAGE,
                BigDecimal.valueOf(15),
                CouponAppliesTo.SPECIFIC_PRODUCTS,
                "[{\"category\":\"MERCHANDISING\"}]",
                null,
                null,
                null,
                false,
                null,
                false,
                null,
                true,
                true,
                true,
                LocalDate.parse("2026-04-07"),
                LocalTime.MIDNIGHT,
                false,
                null,
                null,
                CouponStatus.ACTIVE,
                4,
                CouponTriggerType.NONE,
                null,
                null,
                false,
                "[]",
                "{\"type\":\"percentage_on_quantity\",\"minQuantity\":3}",
                now
        );

        CouponDto dto = couponMapper.toDto(coupon);

        assertThat(dto.getScope()).isNotNull();
        assertThat(dto.getScope().getType()).isEqualTo(CouponScopeType.CATEGORY);
        assertThat(dto.getScope().getCategories()).containsExactly(ProductCategory.MERCHANDISING);
        assertThat(dto.getBenefit()).isNotNull();
        assertThat(dto.getBenefit().getType()).isEqualTo(CouponBenefitType.PERCENTAGE_ON_QUANTITY);
        assertThat(dto.getBenefit().getThresholdQuantity()).isEqualTo(3);
        assertThat(dto.getBenefit().getAdvanced()).isTrue();
    }
}

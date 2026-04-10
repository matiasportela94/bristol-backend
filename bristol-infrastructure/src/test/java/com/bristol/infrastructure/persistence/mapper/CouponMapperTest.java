package com.bristol.infrastructure.persistence.mapper;

import com.bristol.domain.coupon.Coupon;
import com.bristol.domain.coupon.CouponAppliesTo;
import com.bristol.domain.coupon.CouponBenefitType;
import com.bristol.domain.coupon.CouponDiscountType;
import com.bristol.domain.coupon.CouponMethod;
import com.bristol.domain.coupon.CouponScopeType;
import com.bristol.domain.coupon.CouponStatus;
import com.bristol.domain.coupon.CouponTriggerType;
import com.bristol.domain.coupon.CouponValueType;
import com.bristol.domain.user.UserId;
import com.bristol.infrastructure.persistence.entity.CouponEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class CouponMapperTest {

    private final CouponMapper mapper = new CouponMapper();

    @Test
    void shouldRoundTripDraftStatusAndPriority() {
        Instant now = Instant.parse("2026-04-07T12:00:00Z");
        Coupon coupon = Coupon.create(
                "Welcome",
                "WELCOME10",
                "Welcome coupon",
                CouponMethod.CODE,
                CouponDiscountType.ORDER,
                CouponValueType.PERCENTAGE,
                BigDecimal.TEN,
                CouponAppliesTo.ENTIRE_ORDER,
                LocalDate.parse("2026-04-07"),
                LocalTime.MIDNIGHT,
                UserId.generate(),
                now
        ).reconfigure(
                "Welcome Draft",
                "WELCOME10",
                "Draft coupon",
                CouponMethod.CODE,
                CouponDiscountType.ORDER,
                CouponValueType.PERCENTAGE,
                BigDecimal.TEN,
                CouponAppliesTo.ENTIRE_ORDER,
                "[]",
                null,
                null,
                null,
                false,
                null,
                false,
                null,
                false,
                false,
                false,
                LocalDate.parse("2026-04-07"),
                LocalTime.MIDNIGHT,
                false,
                null,
                null,
                CouponStatus.DRAFT,
                9,
                CouponTriggerType.NONE,
                null,
                null,
                false,
                "[]",
                "{}",
                now
        );

        CouponEntity entity = mapper.toEntity(coupon);
        Coupon mappedBack = mapper.toDomain(entity);

        assertThat(entity.getStatus()).isEqualTo(CouponEntity.CouponStatusEnum.DRAFT);
        assertThat(entity.getPriority()).isEqualTo(9);
        assertThat(mappedBack.getStatus()).isEqualTo(CouponStatus.DRAFT);
        assertThat(mappedBack.getPriority()).isEqualTo(9);
    }

    @Test
    void shouldExposeNormalizedScopeAndBenefitFromLegacyFields() {
        Instant now = Instant.parse("2026-04-07T12:00:00Z");
        CouponEntity entity = CouponEntity.builder()
                .id(java.util.UUID.randomUUID())
                .name("2x1 IPA")
                .code("DOSPORUNO")
                .description("Buy x get y")
                .appliesTo(CouponEntity.CouponAppliesToEnum.SPECIFIC_PRODUCTS)
                .selectedItems("[{\"productId\":\"abc-123\"}]")
                .method(CouponEntity.CouponMethodEnum.CODE)
                .discountType(CouponEntity.CouponDiscountTypeEnum.PRODUCT)
                .valueType(CouponEntity.CouponValueTypeEnum.FIXED)
                .value(BigDecimal.ONE)
                .scheduleType(CouponEntity.CouponScheduleTypeEnum.SCHEDULED)
                .startDate(java.time.LocalDateTime.parse("2026-04-07T00:00:00"))
                .status(CouponEntity.CouponStatusEnum.ACTIVE)
                .priority(3)
                .timesUsed(0)
                .isCustomerSpecific(false)
                .combineWithProductDiscounts(false)
                .combineWithOrderDiscounts(false)
                .combineWithShippingDiscounts(false)
                .triggerType(CouponEntity.CouponTriggerTypeEnum.BUY_X_GET_Y)
                .ruleConfig("{\"buyX\":2,\"getY\":1}")
                .appliesToFutureOrders(false)
                .specificCustomers("[]")
                .createdAt(now)
                .updatedAt(now)
                .build();

        Coupon coupon = mapper.toDomain(entity);

        assertThat(coupon.getScope().getType()).isEqualTo(CouponScopeType.SPECIFIC_PRODUCT);
        assertThat(coupon.getBenefit().getType()).isEqualTo(CouponBenefitType.BUY_X_GET_Y);
    }
}

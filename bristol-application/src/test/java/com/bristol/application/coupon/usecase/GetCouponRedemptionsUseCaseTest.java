package com.bristol.application.coupon.usecase;

import com.bristol.application.coupon.dto.CouponRedemptionDto;
import com.bristol.domain.coupon.CouponId;
import com.bristol.domain.coupon.CouponRedemption;
import com.bristol.domain.coupon.CouponRedemptionRepository;
import com.bristol.domain.order.OrderId;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.UserId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetCouponRedemptionsUseCaseTest {

    @Test
    void executeShouldMapCouponRedemptionHistory() {
        CouponRedemptionRepository couponRedemptionRepository = mock(CouponRedemptionRepository.class);
        GetCouponRedemptionsUseCase useCase = new GetCouponRedemptionsUseCase(couponRedemptionRepository);

        CouponId couponId = CouponId.generate();
        CouponRedemption redemption = CouponRedemption.create(
                couponId,
                OrderId.generate(),
                UserId.generate(),
                Money.of(50),
                Instant.parse("2026-04-07T12:00:00Z")
        );

        when(couponRedemptionRepository.findByCouponId(couponId)).thenReturn(List.of(redemption));

        List<CouponRedemptionDto> result = useCase.execute(couponId.asString());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCouponId()).isEqualTo(couponId.asString());
        assertThat(result.get(0).getAppliedAmount()).isEqualByComparingTo("50.00");
    }
}

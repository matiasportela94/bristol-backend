package com.bristol.application.order.usecase;

import com.bristol.application.order.dto.ApplyCouponRequest;
import com.bristol.application.order.dto.OrderDto;
import com.bristol.domain.coupon.CouponDiscountType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplyShippingCouponUseCase {
    private final OrderPromotionApplicationService orderPromotionApplicationService;

    @Transactional
    public OrderDto execute(String orderId, ApplyCouponRequest request) {
        return orderPromotionApplicationService.applyCoupon(
                orderId,
                request.getCouponCode(),
                CouponDiscountType.SHIPPING
        );
    }
}

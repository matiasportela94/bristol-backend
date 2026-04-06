package com.bristol.application.order.usecase;

import com.bristol.application.order.dto.ApplyCouponRequest;
import com.bristol.application.order.dto.OrderDto;
import com.bristol.domain.coupon.Coupon;
import com.bristol.domain.coupon.CouponDiscountType;
import com.bristol.domain.coupon.CouponRepository;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderId;
import com.bristol.domain.order.OrderRepository;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ApplyShippingCouponUseCase {
    private final OrderRepository orderRepository;
    private final CouponRepository couponRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderDto execute(String orderId, ApplyCouponRequest request) {
        OrderId id = new OrderId(orderId);
        Instant now = Instant.now();

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Order not found: " + orderId));

        Coupon coupon = couponRepository.findByCode(request.getCouponCode())
                .orElseThrow(() -> new ValidationException("Invalid coupon code"));

        // Validate coupon is for shipping discount
        if (coupon.getDiscountType() != CouponDiscountType.SHIPPING) {
            throw new ValidationException("This coupon is not valid for shipping discounts");
        }

        // Calculate discount amount
        Money discountAmount = calculateDiscount(coupon, order.getShippingCost());

        // Apply shipping discount
        Order updatedOrder = order.applyShippingDiscount(
                coupon.getId(),
                discountAmount,
                now
        );

        Order savedOrder = orderRepository.save(updatedOrder);
        return orderMapper.toDto(savedOrder);
    }

    private Money calculateDiscount(Coupon coupon, Money shippingCost) {
        BigDecimal discountValue = BigDecimal.ZERO;

        switch (coupon.getValueType()) {
            case PERCENTAGE:
                discountValue = shippingCost.getAmount()
                        .multiply(coupon.getValue())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                break;
            case FIXED:
                discountValue = coupon.getValue();
                break;
        }

        return Money.of(discountValue);
    }
}

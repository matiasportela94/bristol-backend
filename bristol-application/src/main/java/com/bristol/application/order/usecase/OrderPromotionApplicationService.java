package com.bristol.application.order.usecase;

import com.bristol.application.order.dto.OrderDto;
import com.bristol.domain.coupon.Coupon;
import com.bristol.domain.coupon.CouponDiscountType;
import com.bristol.domain.coupon.CouponRepository;
import com.bristol.domain.coupon.CouponRedemptionRepository;
import com.bristol.domain.coupon.PromotionEngine;
import com.bristol.domain.coupon.PromotionEvaluationResult;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderId;
import com.bristol.domain.order.OrderRepository;
import com.bristol.domain.order.OrderStatus;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

/**
 * Shared application orchestration for coupon-backed promotion application.
 */
@Component
@RequiredArgsConstructor
public class OrderPromotionApplicationService {

    private final OrderRepository orderRepository;
    private final CouponRepository couponRepository;
    private final CouponRedemptionRepository couponRedemptionRepository;
    private final OrderMapper orderMapper;
    private final TimeProvider timeProvider;
    private final UserRepository userRepository;

    public OrderDto applyCoupon(String orderId, String couponCode, CouponDiscountType requestedDiscountType) {
        Order order = orderRepository.findById(new OrderId(orderId))
                .orElseThrow(() -> new ValidationException("Order not found: " + orderId));
        ensurePromotionsEditable(order);

        Coupon requestedCoupon = loadRequestedCoupon(couponCode, requestedDiscountType);
        Order repricedOrder = reprice(order,
                requestedDiscountType == CouponDiscountType.ORDER ? Optional.of(requestedCoupon) : Optional.empty(),
                requestedDiscountType == CouponDiscountType.SHIPPING ? Optional.of(requestedCoupon) : Optional.empty(),
                true
        );

        Order savedOrder = orderRepository.save(repricedOrder);
        return orderMapper.toDto(savedOrder);
    }

    public OrderDto repriceOrder(String orderId) {
        Order order = orderRepository.findById(new OrderId(orderId))
                .orElseThrow(() -> new ValidationException("Order not found: " + orderId));
        ensurePromotionsEditable(order);

        Order repricedOrder = reprice(order, Optional.empty(), Optional.empty(), false);
        Order savedOrder = orderRepository.save(repricedOrder);
        return orderMapper.toDto(savedOrder);
    }

    public Order applyRequestedPromotions(Order order, String orderCouponCode, String shippingCouponCode) {
        ensurePromotionsEditable(order);

        Optional<Coupon> requestedOrderCoupon = optionalCouponCode(orderCouponCode)
                .map(code -> loadRequestedCoupon(code, CouponDiscountType.ORDER));
        Optional<Coupon> requestedShippingCoupon = optionalCouponCode(shippingCouponCode)
                .map(code -> loadRequestedCoupon(code, CouponDiscountType.SHIPPING));

        return reprice(order, requestedOrderCoupon, requestedShippingCoupon, true);
    }

    public Order applyRequestedPromotion(Order order, String couponCode) {
        ensurePromotionsEditable(order);

        Optional<Coupon> requestedCoupon = optionalCouponCode(couponCode)
                .map(this::loadRequestedCoupon);

        Optional<Coupon> requestedOrderCoupon = requestedCoupon
                .filter(coupon -> coupon.getDiscountType() == CouponDiscountType.ORDER);
        Optional<Coupon> requestedShippingCoupon = requestedCoupon
                .filter(coupon -> coupon.getDiscountType() == CouponDiscountType.SHIPPING);

        return reprice(order, requestedOrderCoupon, requestedShippingCoupon, true);
    }

    private Coupon loadRequestedCoupon(String couponCode, CouponDiscountType requestedDiscountType) {
        Coupon requestedCoupon = couponRepository.findByCode(couponCode)
                .orElseThrow(() -> new ValidationException("Invalid coupon code"));
        validateRequestedType(requestedCoupon, requestedDiscountType);
        return requestedCoupon;
    }

    private Coupon loadRequestedCoupon(String couponCode) {
        return couponRepository.findByCode(couponCode)
                .orElseThrow(() -> new ValidationException("Invalid coupon code"));
    }

    private Order reprice(
            Order order,
            Optional<Coupon> requestedOrderCoupon,
            Optional<Coupon> requestedShippingCoupon,
            boolean failIfRequestedDropped
    ) {
        PromotionEngine promotionEngine = new PromotionEngine(timeProvider, couponRedemptionRepository);
        String userEmail = resolveUserEmail(order);
        requestedOrderCoupon.ifPresent(coupon -> promotionEngine.validateCoupon(order, coupon, userEmail));
        requestedShippingCoupon.ifPresent(coupon -> promotionEngine.validateCoupon(order, coupon, userEmail));

        List<Coupon> candidates = collectCandidates(order, requestedOrderCoupon, requestedShippingCoupon);
        PromotionEvaluationResult evaluation = promotionEngine.evaluate(order, candidates, userEmail);

        if (failIfRequestedDropped) {
            ensureRequestedPromotionSurvived(evaluation, requestedOrderCoupon, CouponDiscountType.ORDER);
            ensureRequestedPromotionSurvived(evaluation, requestedShippingCoupon, CouponDiscountType.SHIPPING);
        }

        return order.applyPromotions(
                evaluation.repricedItems(),
                evaluation.getOrderPromotion().map(application -> application.coupon().getId()).orElse(null),
                evaluation.getOrderPromotion().map(application -> application.discountAmount()).orElse(Money.zero()),
                evaluation.getShippingPromotion().map(application -> application.coupon().getId()).orElse(null),
                evaluation.getShippingPromotion().map(application -> application.discountAmount()).orElse(Money.zero()),
                timeProvider.now()
        );
    }

    private List<Coupon> collectCandidates(
            Order order,
            Optional<Coupon> requestedOrderCoupon,
            Optional<Coupon> requestedShippingCoupon
    ) {
        LinkedHashMap<String, Coupon> candidates = new LinkedHashMap<>();

        requestedOrderCoupon.ifPresent(coupon -> candidates.put(coupon.getId().getValue().toString(), coupon));
        requestedShippingCoupon.ifPresent(coupon -> candidates.put(coupon.getId().getValue().toString(), coupon));

        couponRepository.findAutomatic()
                .forEach(coupon -> candidates.putIfAbsent(coupon.getId().getValue().toString(), coupon));

        if (requestedOrderCoupon.isEmpty() && order.getOrderDiscountCouponId() != null) {
            couponRepository.findById(order.getOrderDiscountCouponId())
                    .ifPresent(coupon -> candidates.putIfAbsent(coupon.getId().getValue().toString(), coupon));
        }

        if (requestedShippingCoupon.isEmpty() && order.getShippingDiscountCouponId() != null) {
            couponRepository.findById(order.getShippingDiscountCouponId())
                    .ifPresent(coupon -> candidates.putIfAbsent(coupon.getId().getValue().toString(), coupon));
        }

        return new ArrayList<>(candidates.values());
    }

    private void ensureRequestedPromotionSurvived(
            PromotionEvaluationResult evaluation,
            Optional<Coupon> requestedCoupon,
            CouponDiscountType discountType
    ) {
        if (requestedCoupon.isPresent() && !evaluation.containsCoupon(requestedCoupon.orElseThrow().getId())) {
            if (discountType == CouponDiscountType.ORDER) {
                throw new ValidationException("This order coupon cannot be combined with the promotions already applied to the order");
            }
            if (discountType == CouponDiscountType.SHIPPING) {
                throw new ValidationException("This shipping coupon cannot be combined with the promotions already applied to the order");
            }
            throw new ValidationException("The requested coupon cannot be combined with the promotions already applied to the order");
        }
    }

    private Optional<String> optionalCouponCode(String couponCode) {
        return Optional.ofNullable(couponCode)
                .map(String::trim)
                .filter(value -> !value.isEmpty());
    }

    private void validateRequestedType(Coupon coupon, CouponDiscountType requestedDiscountType) {
        if (coupon.getDiscountType() != requestedDiscountType) {
            if (requestedDiscountType == CouponDiscountType.ORDER) {
                throw new ValidationException("This coupon is not valid for order discounts");
            }
            if (requestedDiscountType == CouponDiscountType.SHIPPING) {
                throw new ValidationException("This coupon is not valid for shipping discounts");
            }
            throw new ValidationException("This coupon is not valid for the requested discount type");
        }
    }

    private void ensurePromotionsEditable(Order order) {
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT
                && order.getStatus() != OrderStatus.PAYMENT_IN_PROCESS) {
            throw new ValidationException("Promotions can only be changed while the order is awaiting payment");
        }
    }

    private String resolveUserEmail(Order order) {
        return userRepository.findById(order.getUserId())
                .map(user -> user.getEmail())
                .orElse(null);
    }
}

package com.bristol.application.payment.usecase;

import com.bristol.application.delivery.service.DeliverySchedulingService;
import com.bristol.application.order.service.StockManagementService;
import com.bristol.application.order.usecase.CouponRedemptionApplicationService;
import com.bristol.application.payment.dto.PaymentDto;
import com.bristol.application.payment.dto.RejectPaymentRequest;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderRepository;
import com.bristol.domain.order.OrderStatus;
import com.bristol.domain.payment.Payment;
import com.bristol.domain.payment.PaymentId;
import com.bristol.domain.payment.PaymentRepository;
import com.bristol.domain.payment.PaymentStatus;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RejectPaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final StockManagementService stockManagementService;
    private final CouponRedemptionApplicationService couponRedemptionApplicationService;
    private final DeliverySchedulingService deliverySchedulingService;
    private final PaymentMapper paymentMapper;
    private final TimeProvider timeProvider;

    @Transactional
    public PaymentDto execute(String paymentId, RejectPaymentRequest request) {
        Payment payment = paymentRepository.findById(new PaymentId(paymentId))
                .orElseThrow(() -> new ValidationException("Payment not found: " + paymentId));

        if (payment.getStatus() == PaymentStatus.REJECTED) {
            return paymentMapper.toDto(payment);
        }

        Order order = orderRepository.findById(payment.getOrderId())
                .orElseThrow(() -> new ValidationException("Order not found: " + payment.getOrderId().getValue()));

        handleOrderRejection(order);

        Payment rejectedPayment = payment.reject(request.getReason(), timeProvider.now());
        Payment savedPayment = paymentRepository.save(rejectedPayment);
        return paymentMapper.toDto(savedPayment);
    }

    private void handleOrderRejection(Order order) {
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new ValidationException("Cannot reject payment for a cancelled order");
        }
        if (order.getStatus() == OrderStatus.PAID
                || order.getStatus() == OrderStatus.PROCESSING
                || order.getStatus() == OrderStatus.SHIPPED
                || order.getStatus() == OrderStatus.DELIVERED) {
            throw new ValidationException("Cannot reject payment for an already paid order");
        }

        if (order.getStatus() == OrderStatus.PAYMENT_FAILED) {
            return;
        }

        if (order.isStockUpdated()) {
            stockManagementService.restoreStockForOrder(order);
        }

        Order savedOrder = orderRepository.save(order.markPaymentFailed(timeProvider.now()));
        couponRedemptionApplicationService.clearOrderRedemptions(savedOrder, timeProvider.now());
        deliverySchedulingService.cancelScheduledDelivery(savedOrder);
    }
}

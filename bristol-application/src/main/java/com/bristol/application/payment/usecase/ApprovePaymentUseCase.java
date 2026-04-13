package com.bristol.application.payment.usecase;

import com.bristol.application.delivery.service.DeliverySchedulingService;
import com.bristol.application.order.usecase.CouponRedemptionApplicationService;
import com.bristol.application.payment.dto.ApprovePaymentRequest;
import com.bristol.application.payment.dto.PaymentDto;
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
public class ApprovePaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final CouponRedemptionApplicationService couponRedemptionApplicationService;
    private final DeliverySchedulingService deliverySchedulingService;
    private final PaymentMapper paymentMapper;
    private final TimeProvider timeProvider;

    @Transactional
    public PaymentDto execute(String paymentId, ApprovePaymentRequest request) {
        Payment payment = paymentRepository.findById(new PaymentId(paymentId))
                .orElseThrow(() -> new ValidationException("Payment not found: " + paymentId));

        if (payment.getStatus() == PaymentStatus.APPROVED) {
            return paymentMapper.toDto(payment);
        }

        if (paymentRepository.findApprovedByOrderId(payment.getOrderId())
                .filter(existing -> !existing.getId().equals(payment.getId()))
                .isPresent()) {
            throw new ValidationException("Order already has another approved payment");
        }

        Order order = orderRepository.findById(payment.getOrderId())
                .orElseThrow(() -> new ValidationException("Order not found: " + payment.getOrderId().getValue()));

        Order paidOrder = ensureOrderIsPaid(order);
        couponRedemptionApplicationService.recordPaidOrderRedemptions(paidOrder, timeProvider.now());
        deliverySchedulingService.ensureScheduledForPaidOrder(paidOrder);

        Payment approvedPayment = payment.approve(
                request != null ? request.getProviderReference() : null,
                timeProvider.now()
        );

        Payment savedPayment = paymentRepository.save(approvedPayment);
        return paymentMapper.toDto(savedPayment);
    }

    private Order ensureOrderIsPaid(Order order) {
        if (order.getStatus() == OrderStatus.PENDING_PAYMENT
                || order.getStatus() == OrderStatus.PAYMENT_IN_PROCESS) {
            return orderRepository.save(order.markAsPaid(timeProvider.now()));
        }

        if (order.getStatus() == OrderStatus.PAID
                || order.getStatus() == OrderStatus.PROCESSING
                || order.getStatus() == OrderStatus.SHIPPED
                || order.getStatus() == OrderStatus.DELIVERED) {
            return order;
        }

        throw new ValidationException("Cannot approve payment for order in status: " + order.getStatus());
    }
}

package com.bristol.application.payment.usecase;

import com.bristol.application.payment.dto.CreatePaymentRequest;
import com.bristol.application.payment.dto.PaymentDto;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderId;
import com.bristol.domain.order.OrderRepository;
import com.bristol.domain.order.OrderStatus;
import com.bristol.domain.payment.Payment;
import com.bristol.domain.payment.PaymentRepository;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreatePaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;
    private final TimeProvider timeProvider;

    @Transactional
    public PaymentDto execute(CreatePaymentRequest request) {
        Order order = orderRepository.findById(new OrderId(request.getOrderId()))
                .orElseThrow(() -> new ValidationException("Order not found: " + request.getOrderId()));

        ensureOrderCanReceivePayment(order);

        if (order.getStatus() == OrderStatus.PENDING_PAYMENT) {
            orderRepository.save(order.markPaymentInProcess(timeProvider.now()));
        }

        Payment payment = Payment.create(
                order.getId(),
                order.getUserId(),
                request.getProvider(),
                request.getProviderReference(),
                order.getFinalTotal(),
                resolveCurrency(request.getCurrency()),
                timeProvider.now()
        );

        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toDto(savedPayment);
    }

    private void ensureOrderCanReceivePayment(Order order) {
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT
                && order.getStatus() != OrderStatus.PAYMENT_IN_PROCESS) {
            throw new ValidationException("Payments can only be created for orders awaiting payment");
        }

        if (paymentRepository.findApprovedByOrderId(order.getId()).isPresent()) {
            throw new ValidationException("Order already has an approved payment");
        }
    }

    private String resolveCurrency(String currency) {
        if (currency == null || currency.trim().isEmpty()) {
            return "ARS";
        }
        return currency.trim().toUpperCase();
    }
}

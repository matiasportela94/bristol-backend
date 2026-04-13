package com.bristol.application.payment.usecase;

import com.bristol.application.payment.dto.PaymentDto;
import com.bristol.domain.order.OrderRepository;
import com.bristol.domain.payment.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentMapper {

    private final OrderRepository orderRepository;

    public PaymentDto toDto(Payment payment) {
        Long orderNumber = orderRepository.findById(payment.getOrderId())
                .map(order -> order.getOrderNumber())
                .orElse(null);

        return PaymentDto.builder()
                .id(payment.getId().getValue().toString())
                .paymentNumber(payment.getPaymentNumber())
                .orderId(payment.getOrderId().getValue().toString())
                .orderNumber(orderNumber)
                .userId(payment.getUserId().getValue().toString())
                .status(payment.getStatus())
                .provider(payment.getProvider())
                .providerReference(payment.getProviderReference())
                .amount(payment.getAmount().getAmount())
                .currency(payment.getCurrency())
                .approvedAt(payment.getApprovedAt())
                .rejectedAt(payment.getRejectedAt())
                .rejectionReason(payment.getRejectionReason())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}

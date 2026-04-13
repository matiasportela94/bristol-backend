package com.bristol.application.payment.usecase;

import com.bristol.application.payment.dto.PaymentDto;
import com.bristol.domain.order.OrderId;
import com.bristol.domain.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetPaymentsByOrderUseCase {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Transactional(readOnly = true)
    public List<PaymentDto> execute(String orderId) {
        return paymentRepository.findByOrderId(new OrderId(orderId)).stream()
                .map(paymentMapper::toDto)
                .collect(Collectors.toList());
    }
}

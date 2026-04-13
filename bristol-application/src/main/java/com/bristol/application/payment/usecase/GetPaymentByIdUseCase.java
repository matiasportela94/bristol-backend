package com.bristol.application.payment.usecase;

import com.bristol.application.payment.dto.PaymentDto;
import com.bristol.domain.payment.PaymentId;
import com.bristol.domain.payment.PaymentRepository;
import com.bristol.domain.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetPaymentByIdUseCase {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Transactional(readOnly = true)
    public PaymentDto execute(String paymentId) {
        return paymentRepository.findById(new PaymentId(paymentId))
                .map(paymentMapper::toDto)
                .orElseThrow(() -> new ValidationException("Payment not found: " + paymentId));
    }
}

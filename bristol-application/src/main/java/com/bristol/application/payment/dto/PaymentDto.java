package com.bristol.application.payment.dto;

import com.bristol.domain.payment.PaymentProvider;
import com.bristol.domain.payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {

    private String id;
    private Long paymentNumber;
    private String orderId;
    private Long orderNumber;
    private String userId;
    private PaymentStatus status;
    private PaymentProvider provider;
    private String providerReference;
    private BigDecimal amount;
    private String currency;
    private Instant approvedAt;
    private Instant rejectedAt;
    private String rejectionReason;
    private Instant createdAt;
    private Instant updatedAt;
}

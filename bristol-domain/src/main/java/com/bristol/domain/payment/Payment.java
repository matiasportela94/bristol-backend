package com.bristol.domain.payment;

import com.bristol.domain.order.OrderId;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.UserId;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder(toBuilder = true)
public class Payment {

    private final PaymentId id;
    private final Long paymentNumber;
    private final OrderId orderId;
    private final UserId userId;
    private final PaymentStatus status;
    private final PaymentProvider provider;
    private final String providerReference;
    private final Money amount;
    private final String currency;
    private final Instant approvedAt;
    private final Instant rejectedAt;
    private final String rejectionReason;
    private final Instant createdAt;
    private final Instant updatedAt;

    public static Payment create(
            OrderId orderId,
            UserId userId,
            PaymentProvider provider,
            String providerReference,
            Money amount,
            String currency,
            Instant now
    ) {
        validate(orderId, userId, provider, amount, currency, now);

        return Payment.builder()
                .id(PaymentId.generate())
                .paymentNumber(null)
                .orderId(orderId)
                .userId(userId)
                .status(PaymentStatus.PENDING)
                .provider(provider)
                .providerReference(normalize(providerReference))
                .amount(amount)
                .currency(normalizeRequired(currency, "Currency is required"))
                .approvedAt(null)
                .rejectedAt(null)
                .rejectionReason(null)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public Payment approve(String providerReference, Instant now) {
        if (status == PaymentStatus.APPROVED) {
            throw new ValidationException("Payment is already approved");
        }
        if (status == PaymentStatus.REJECTED) {
            throw new ValidationException("Rejected payments cannot be approved");
        }

        return this.toBuilder()
                .status(PaymentStatus.APPROVED)
                .providerReference(resolveProviderReference(providerReference))
                .approvedAt(now)
                .rejectedAt(null)
                .rejectionReason(null)
                .updatedAt(now)
                .build();
    }

    public Payment reject(String reason, Instant now) {
        if (status == PaymentStatus.REJECTED) {
            throw new ValidationException("Payment is already rejected");
        }
        if (status == PaymentStatus.APPROVED) {
            throw new ValidationException("Approved payments cannot be rejected");
        }

        return this.toBuilder()
                .status(PaymentStatus.REJECTED)
                .rejectedAt(now)
                .rejectionReason(normalizeRequired(reason, "Rejection reason is required"))
                .updatedAt(now)
                .build();
    }

    private static void validate(
            OrderId orderId,
            UserId userId,
            PaymentProvider provider,
            Money amount,
            String currency,
            Instant now
    ) {
        if (orderId == null) {
            throw new ValidationException("Order ID is required");
        }
        if (userId == null) {
            throw new ValidationException("User ID is required");
        }
        if (provider == null) {
            throw new ValidationException("Payment provider is required");
        }
        if (amount == null || amount.isNegative()) {
            throw new ValidationException("Payment amount must be zero or positive");
        }
        normalizeRequired(currency, "Currency is required");
        if (now == null) {
            throw new ValidationException("Current time is required");
        }
    }

    private String resolveProviderReference(String providerReference) {
        String normalizedReference = normalize(providerReference);
        return normalizedReference != null ? normalizedReference : this.providerReference;
    }

    private static String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static String normalizeRequired(String value, String message) {
        String normalized = normalize(value);
        if (normalized == null) {
            throw new ValidationException(message);
        }
        return normalized;
    }
}

package com.bristol.domain.distributor;

import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.user.UserId;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Distributor domain entity.
 * Represents a business distributor with extended profile information.
 */
@Getter
@Builder(toBuilder = true)
public class Distributor {

    private final DistributorId id;
    private final UserId userId;
    private final String address;
    private final String phone;
    private final String dni;
    private final String cuit;
    private final String razonSocial;
    private final LocalDate dateOfBirth;
    private final DeliveryZoneId deliveryZoneId;
    private final DistributorStatus status;

    // Metrics
    private final Integer totalOrders;
    private final BigDecimal totalSpent;
    private final Integer totalBeers;
    private final BigDecimal totalProfit;
    private final Instant lastOrderAt;
    private final Instant lastSignInAt;
    private final Instant emailConfirmedAt;

    private final Instant createdAt;
    private final Instant updatedAt;

    /**
     * Factory method to create a new distributor (pending status).
     */
    public static Distributor create(
            UserId userId,
            String address,
            String phone,
            String dni,
            String cuit,
            String razonSocial,
            LocalDate dateOfBirth,
            DeliveryZoneId deliveryZoneId,
            Instant now
    ) {
        validateBusinessInfo(cuit, razonSocial);

        return Distributor.builder()
                .id(DistributorId.generate())
                .userId(userId)
                .address(address)
                .phone(phone)
                .dni(dni)
                .cuit(cuit)
                .razonSocial(razonSocial)
                .dateOfBirth(dateOfBirth)
                .deliveryZoneId(deliveryZoneId)
                .status(DistributorStatus.PENDING)
                .totalOrders(0)
                .totalSpent(BigDecimal.ZERO)
                .totalBeers(0)
                .totalProfit(BigDecimal.ZERO)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Approve distributor registration.
     */
    public Distributor approve(Instant now) {
        if (status == DistributorStatus.APPROVED) {
            throw new ValidationException("Distributor is already approved");
        }

        return this.toBuilder()
                .status(DistributorStatus.APPROVED)
                .updatedAt(now)
                .build();
    }

    /**
     * Reject distributor registration.
     */
    public Distributor reject(Instant now) {
        if (status == DistributorStatus.REJECTED) {
            throw new ValidationException("Distributor is already rejected");
        }

        return this.toBuilder()
                .status(DistributorStatus.REJECTED)
                .updatedAt(now)
                .build();
    }

    /**
     * Update business information.
     */
    public Distributor updateBusinessInfo(
            String address,
            String phone,
            String cuit,
            String razonSocial,
            DeliveryZoneId deliveryZoneId,
            Instant now
    ) {
        validateBusinessInfo(cuit, razonSocial);

        return this.toBuilder()
                .address(address)
                .phone(phone)
                .cuit(cuit)
                .razonSocial(razonSocial)
                .deliveryZoneId(deliveryZoneId)
                .updatedAt(now)
                .build();
    }

    /**
     * Record a new order for this distributor.
     */
    public Distributor recordOrder(BigDecimal orderAmount, int beersCount, Instant now) {
        return this.toBuilder()
                .totalOrders(totalOrders + 1)
                .totalSpent(totalSpent.add(orderAmount))
                .totalBeers(totalBeers + beersCount)
                .lastOrderAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Update last sign-in timestamp.
     */
    public Distributor recordSignIn(Instant now) {
        return this.toBuilder()
                .lastSignInAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Confirm email.
     */
    public Distributor confirmEmail(Instant now) {
        return this.toBuilder()
                .emailConfirmedAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Check if distributor is approved.
     */
    public boolean isApproved() {
        return status == DistributorStatus.APPROVED;
    }

    /**
     * Check if distributor is pending approval.
     */
    public boolean isPending() {
        return status == DistributorStatus.PENDING;
    }

    private static void validateBusinessInfo(String cuit, String razonSocial) {
        if (cuit == null || cuit.trim().isEmpty()) {
            throw new ValidationException("CUIT is required");
        }
        if (razonSocial == null || razonSocial.trim().isEmpty()) {
            throw new ValidationException("Razon Social is required");
        }
        // Could add CUIT format validation here
    }
}

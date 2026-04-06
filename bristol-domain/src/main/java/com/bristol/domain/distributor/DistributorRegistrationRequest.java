package com.bristol.domain.distributor;

import com.bristol.domain.shared.exception.ValidationException;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * Distributor registration request aggregate.
 * Represents a pending request from someone wanting to become a distributor.
 */
@Getter
@Builder(toBuilder = true)
public class DistributorRegistrationRequest {

    private final DistributorRegistrationRequestId id;
    private final String razonSocial;
    private final String cuit;
    private final String email;
    private final String telefono;
    private final String provincia;
    private final String ciudad;
    private final String direccion;
    private final String codigoPostal;
    private final RegistrationStatus status;
    private final String rejectionReason;
    private final Instant createdAt;
    private final Instant updatedAt;

    /**
     * Create a new pending registration request.
     */
    public static DistributorRegistrationRequest create(
            String razonSocial,
            String cuit,
            String email,
            String telefono,
            String provincia,
            String ciudad,
            String direccion,
            String codigoPostal,
            Instant now
    ) {
        validate(razonSocial, cuit, email, telefono, provincia, ciudad, direccion);

        return DistributorRegistrationRequest.builder()
                .id(DistributorRegistrationRequestId.generate())
                .razonSocial(razonSocial)
                .cuit(cuit)
                .email(email)
                .telefono(telefono)
                .provincia(provincia)
                .ciudad(ciudad)
                .direccion(direccion)
                .codigoPostal(codigoPostal)
                .status(RegistrationStatus.PENDING)
                .rejectionReason(null)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Approve the registration request.
     */
    public DistributorRegistrationRequest approve(Instant now) {
        if (status != RegistrationStatus.PENDING) {
            throw new ValidationException("Only pending requests can be approved");
        }

        return this.toBuilder()
                .status(RegistrationStatus.APPROVED)
                .updatedAt(now)
                .build();
    }

    /**
     * Reject the registration request.
     */
    public DistributorRegistrationRequest reject(String reason, Instant now) {
        if (status != RegistrationStatus.PENDING) {
            throw new ValidationException("Only pending requests can be rejected");
        }

        if (reason == null || reason.isBlank()) {
            throw new ValidationException("Rejection reason is required");
        }

        return this.toBuilder()
                .status(RegistrationStatus.REJECTED)
                .rejectionReason(reason)
                .updatedAt(now)
                .build();
    }

    private static void validate(
            String razonSocial,
            String cuit,
            String email,
            String telefono,
            String provincia,
            String ciudad,
            String direccion
    ) {
        if (razonSocial == null || razonSocial.isBlank()) {
            throw new ValidationException("Razón social is required");
        }

        if (cuit == null || cuit.isBlank()) {
            throw new ValidationException("CUIT is required");
        }

        if (email == null || email.isBlank()) {
            throw new ValidationException("Email is required");
        }

        if (telefono == null || telefono.isBlank()) {
            throw new ValidationException("Telefono is required");
        }

        if (provincia == null || provincia.isBlank()) {
            throw new ValidationException("Provincia is required");
        }

        if (ciudad == null || ciudad.isBlank()) {
            throw new ValidationException("Ciudad is required");
        }

        if (direccion == null || direccion.isBlank()) {
            throw new ValidationException("Direccion is required");
        }
    }
}

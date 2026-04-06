package com.bristol.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity for distributor registration requests.
 */
@Entity
@Table(name = "distributor_registration_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistributorRegistrationRequestEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "razon_social", nullable = false)
    private String razonSocial;

    @Column(name = "cuit", nullable = false)
    private String cuit;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "telefono", nullable = false)
    private String telefono;

    @Column(name = "provincia")
    private String provincia;

    @Column(name = "ciudad")
    private String ciudad;

    @Column(name = "direccion", nullable = false)
    private String direccion;

    @Column(name = "codigo_postal")
    private String codigoPostal;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private com.bristol.domain.distributor.RegistrationStatus status;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}

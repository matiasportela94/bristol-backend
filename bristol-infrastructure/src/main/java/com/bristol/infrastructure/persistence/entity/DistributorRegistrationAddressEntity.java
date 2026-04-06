package com.bristol.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "distributor_registration_addresses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistributorRegistrationAddressEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "registration_request_id", nullable = false)
    private UUID registrationRequestId;

    @Column(name = "address_line1", nullable = false)
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "province", nullable = false)
    private String province;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "delivery_zone_id", nullable = false)
    private UUID deliveryZoneId;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}

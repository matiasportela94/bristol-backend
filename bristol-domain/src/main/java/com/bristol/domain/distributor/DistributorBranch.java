package com.bristol.domain.distributor;

import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.shared.exception.ValidationException;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * Branch of a distributor (e.g. a franchise location).
 * Each branch carries its own shipping address and delivery zone — this is where orders are received.
 * Each branch can have its own user who sees only that branch's orders.
 */
@Getter
@Builder(toBuilder = true)
public class DistributorBranch {

    private final DistributorBranchId id;
    private final DistributorId distributorId;
    private final String name;
    private final String address;
    private final String city;
    private final String province;
    private final String codigoPostal;
    private final DeliveryZoneId deliveryZoneId;
    private final boolean active;
    private final Instant createdAt;
    private final Instant updatedAt;

    public static DistributorBranch create(
            DistributorId distributorId,
            String name,
            String address,
            String city,
            String province,
            String codigoPostal,
            DeliveryZoneId deliveryZoneId,
            Instant now
    ) {
        if (distributorId == null) throw new ValidationException("Branch must belong to a distributor");
        if (name == null || name.isBlank()) throw new ValidationException("Branch name is required");
        if (deliveryZoneId == null) throw new ValidationException("Branch delivery zone is required");

        return DistributorBranch.builder()
                .id(DistributorBranchId.generate())
                .distributorId(distributorId)
                .name(name.trim())
                .address(address)
                .city(city)
                .province(province)
                .codigoPostal(codigoPostal)
                .deliveryZoneId(deliveryZoneId)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public DistributorBranch update(
            String name, String address, String city, String province,
            String codigoPostal, DeliveryZoneId deliveryZoneId, Instant now
    ) {
        if (name == null || name.isBlank()) throw new ValidationException("Branch name is required");
        if (deliveryZoneId == null) throw new ValidationException("Branch delivery zone is required");
        return this.toBuilder()
                .name(name.trim())
                .address(address)
                .city(city)
                .province(province)
                .codigoPostal(codigoPostal)
                .deliveryZoneId(deliveryZoneId)
                .updatedAt(now)
                .build();
    }

    public DistributorBranch deactivate(Instant now) {
        return this.toBuilder().active(false).updatedAt(now).build();
    }

    public DistributorBranch activate(Instant now) {
        return this.toBuilder().active(true).updatedAt(now).build();
    }
}

package com.bristol.domain.distributor;

import com.bristol.domain.shared.valueobject.EntityId;

import java.util.UUID;

/**
 * Value object for DistributorRegistrationRequest ID.
 */
public class DistributorRegistrationRequestId extends EntityId {

    public DistributorRegistrationRequestId(UUID value) {
        super(value);
    }

    public static DistributorRegistrationRequestId generate() {
        return new DistributorRegistrationRequestId(UUID.randomUUID());
    }
}

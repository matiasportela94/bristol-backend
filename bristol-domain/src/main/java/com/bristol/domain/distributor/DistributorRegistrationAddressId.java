package com.bristol.domain.distributor;

import com.bristol.domain.shared.valueobject.EntityId;

import java.util.UUID;

public class DistributorRegistrationAddressId extends EntityId {

    public DistributorRegistrationAddressId(UUID value) {
        super(value);
    }

    public static DistributorRegistrationAddressId generate() {
        return new DistributorRegistrationAddressId(UUID.randomUUID());
    }
}

package com.bristol.domain.distributor;

import com.bristol.domain.shared.valueobject.EntityId;

import java.util.UUID;

public class DistributorBranchId extends EntityId {

    public DistributorBranchId(UUID value) {
        super(value);
    }

    public DistributorBranchId(String value) {
        super(value);
    }

    public static DistributorBranchId generate() {
        return new DistributorBranchId(UUID.randomUUID());
    }
}

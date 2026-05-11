package com.bristol.domain.catalog;

import com.bristol.domain.shared.valueobject.EntityId;

import java.util.UUID;

public class MerchTypeId extends EntityId {
    public MerchTypeId(UUID value) {
        super(value);
    }

    public MerchTypeId(String value) {
        super(value);
    }

    public static MerchTypeId generate() {
        return new MerchTypeId(UUID.randomUUID());
    }
}

package com.bristol.domain.catalog;

import com.bristol.domain.shared.valueobject.EntityId;

import java.util.UUID;

public class SpecialTypeId extends EntityId {
    public SpecialTypeId(UUID value) {
        super(value);
    }

    public SpecialTypeId(String value) {
        super(value);
    }

    public static SpecialTypeId generate() {
        return new SpecialTypeId(UUID.randomUUID());
    }
}

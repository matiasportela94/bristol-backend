package com.bristol.domain.address;

import com.bristol.domain.shared.valueobject.EntityId;

import java.util.UUID;

/**
 * Type-safe identifier for UserAddress entities.
 */
public class UserAddressId extends EntityId {

    public UserAddressId(UUID value) {
        super(value);
    }

    public UserAddressId(String value) {
        super(value);
    }

    public static UserAddressId generate() {
        return new UserAddressId(UUID.randomUUID());
    }
}

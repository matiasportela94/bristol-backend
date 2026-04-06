package com.bristol.domain.user;

import com.bristol.domain.shared.valueobject.EntityId;

import java.util.UUID;

/**
 * Type-safe identifier for User entities.
 */
public class UserId extends EntityId {

    public UserId(UUID value) {
        super(value);
    }

    public UserId(String value) {
        super(value);
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }
}

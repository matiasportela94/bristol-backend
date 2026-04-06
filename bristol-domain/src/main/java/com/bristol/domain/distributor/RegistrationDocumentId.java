package com.bristol.domain.distributor;

import com.bristol.domain.shared.valueobject.EntityId;

import java.util.UUID;

/**
 * Value object for RegistrationDocument ID.
 */
public class RegistrationDocumentId extends EntityId {

    public RegistrationDocumentId(UUID value) {
        super(value);
    }

    public static RegistrationDocumentId generate() {
        return new RegistrationDocumentId(UUID.randomUUID());
    }
}

package com.bristol.domain.shared.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * Base class for type-safe entity identifiers.
 * Wraps UUID to provide strong typing and prevent mixing different entity IDs.
 */
public abstract class EntityId {

    private final UUID value;

    protected EntityId(UUID value) {
        this.value = Objects.requireNonNull(value, "Entity ID cannot be null");
    }

    protected EntityId(String value) {
        this.value = UUID.fromString(Objects.requireNonNull(value, "Entity ID cannot be null"));
    }

    public UUID getValue() {
        return value;
    }

    public String asString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityId entityId = (EntityId) o;
        return Objects.equals(value, entityId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

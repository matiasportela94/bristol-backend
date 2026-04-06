package com.bristol.domain.delivery;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * Delivery Zone aggregate root.
 * Represents a geographic delivery zone.
 */
@Getter
@Builder
public class DeliveryZone {

    private final DeliveryZoneId id;
    private final String name;
    private final String description;
    private final boolean isActive;
    private final Instant createdAt;
    private final Instant updatedAt;

    /**
     * Factory method to create a new delivery zone.
     */
    public static DeliveryZone create(
            String name,
            String description,
            Instant now
    ) {
        validate(name);

        return DeliveryZone.builder()
                .id(DeliveryZoneId.generate())
                .name(name)
                .description(description)
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Update delivery zone information.
     */
    public DeliveryZone update(
            String name,
            String description,
            Instant now
    ) {
        validate(name);

        return DeliveryZone.builder()
                .id(this.id)
                .name(name)
                .description(description)
                .isActive(this.isActive)
                .createdAt(this.createdAt)
                .updatedAt(now)
                .build();
    }

    /**
     * Activate the delivery zone.
     */
    public DeliveryZone activate(Instant now) {
        return DeliveryZone.builder()
                .id(this.id)
                .name(this.name)
                .description(this.description)
                .isActive(true)
                .createdAt(this.createdAt)
                .updatedAt(now)
                .build();
    }

    /**
     * Deactivate the delivery zone.
     */
    public DeliveryZone deactivate(Instant now) {
        return DeliveryZone.builder()
                .id(this.id)
                .name(this.name)
                .description(this.description)
                .isActive(false)
                .createdAt(this.createdAt)
                .updatedAt(now)
                .build();
    }

    private static void validate(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Delivery zone name cannot be blank");
        }
    }
}

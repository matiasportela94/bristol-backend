package com.bristol.domain.catalog;

import com.bristol.domain.shared.exception.ValidationException;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * MerchType aggregate root.
 * Represents a catalog entry for merchandise types (Remera, Buzo, Vaso, etc.).
 * Allows dynamic creation of merchandise types without code changes.
 */
@Getter
@Builder(toBuilder = true)
public class MerchType {

    private final MerchTypeId id;
    private final String code;              // Unique identifier (e.g., "REMERA", "BUZO", "VASO")
    private final String name;              // Display name (e.g., "Remera", "Buzo", "Vaso")
    private final String description;       // Detailed description
    private final MerchCategory category;   // Category (CLOTHING, GLASSWARE, ACCESSORIES)
    private final boolean active;           // Soft delete flag
    private final Integer displayOrder;     // Order for UI display
    private final Instant createdAt;
    private final Instant updatedAt;

    /**
     * Factory method to create a new merch type.
     */
    public static MerchType create(
            String code,
            String name,
            String description,
            MerchCategory category,
            Instant now
    ) {
        validateMerchType(code, name, category);

        return MerchType.builder()
                .id(MerchTypeId.generate())
                .code(normalizeCode(code))
                .name(name.trim())
                .description(description != null ? description.trim() : null)
                .category(category)
                .active(true)
                .displayOrder(0)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Update merch type information.
     */
    public MerchType update(
            String name,
            String description,
            MerchCategory category,
            Integer displayOrder,
            Instant now
    ) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Merch type name cannot be empty");
        }
        if (category == null) {
            throw new ValidationException("Merch type category is required");
        }

        return this.toBuilder()
                .name(name.trim())
                .description(description != null ? description.trim() : null)
                .category(category)
                .displayOrder(displayOrder != null ? displayOrder : this.displayOrder)
                .updatedAt(now)
                .build();
    }

    /**
     * Deactivate (soft delete) the merch type.
     */
    public MerchType deactivate(Instant now) {
        return this.toBuilder()
                .active(false)
                .updatedAt(now)
                .build();
    }

    /**
     * Reactivate the merch type.
     */
    public MerchType reactivate(Instant now) {
        return this.toBuilder()
                .active(true)
                .updatedAt(now)
                .build();
    }

    /**
     * Update display order for UI sorting.
     */
    public MerchType updateDisplayOrder(Integer displayOrder, Instant now) {
        if (displayOrder == null || displayOrder < 0) {
            throw new ValidationException("Display order must be non-negative");
        }

        return this.toBuilder()
                .displayOrder(displayOrder)
                .updatedAt(now)
                .build();
    }

    private static void validateMerchType(String code, String name, MerchCategory category) {
        if (code == null || code.trim().isEmpty()) {
            throw new ValidationException("Merch type code is required");
        }
        if (code.length() > 50) {
            throw new ValidationException("Merch type code cannot exceed 50 characters");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Merch type name is required");
        }
        if (name.length() > 100) {
            throw new ValidationException("Merch type name cannot exceed 100 characters");
        }
        if (category == null) {
            throw new ValidationException("Merch type category is required");
        }
    }

    private static String normalizeCode(String code) {
        return code.trim().toUpperCase().replace(" ", "_").replace("-", "_");
    }
}

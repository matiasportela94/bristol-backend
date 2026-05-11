package com.bristol.domain.catalog;

import com.bristol.domain.shared.exception.ValidationException;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * SpecialType aggregate root.
 * Represents a catalog entry for special product types (Ploteo, Evento, Catering, etc.).
 * Allows dynamic creation of special types without code changes.
 */
@Getter
@Builder(toBuilder = true)
public class SpecialType {

    private final SpecialTypeId id;
    private final String code;              // Unique identifier (e.g., "PLOTEO", "EVENTO")
    private final String name;              // Display name (e.g., "Ploteo de Vehículo", "Evento Privado")
    private final String description;       // Detailed description
    private final boolean requiresQuote;    // If true, price is quoted per customer
    private final boolean active;           // Soft delete flag
    private final Integer displayOrder;     // Order for UI display
    private final Instant createdAt;
    private final Instant updatedAt;

    /**
     * Factory method to create a new special type.
     */
    public static SpecialType create(
            String code,
            String name,
            String description,
            boolean requiresQuote,
            Instant now
    ) {
        validateSpecialType(code, name);

        return SpecialType.builder()
                .id(SpecialTypeId.generate())
                .code(normalizeCode(code))
                .name(name.trim())
                .description(description != null ? description.trim() : null)
                .requiresQuote(requiresQuote)
                .active(true)
                .displayOrder(0)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Update special type information.
     */
    public SpecialType update(
            String name,
            String description,
            boolean requiresQuote,
            Integer displayOrder,
            Instant now
    ) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Special type name cannot be empty");
        }

        return this.toBuilder()
                .name(name.trim())
                .description(description != null ? description.trim() : null)
                .requiresQuote(requiresQuote)
                .displayOrder(displayOrder != null ? displayOrder : this.displayOrder)
                .updatedAt(now)
                .build();
    }

    /**
     * Deactivate (soft delete) the special type.
     */
    public SpecialType deactivate(Instant now) {
        return this.toBuilder()
                .active(false)
                .updatedAt(now)
                .build();
    }

    /**
     * Reactivate the special type.
     */
    public SpecialType reactivate(Instant now) {
        return this.toBuilder()
                .active(true)
                .updatedAt(now)
                .build();
    }

    /**
     * Update display order for UI sorting.
     */
    public SpecialType updateDisplayOrder(Integer displayOrder, Instant now) {
        if (displayOrder == null || displayOrder < 0) {
            throw new ValidationException("Display order must be non-negative");
        }

        return this.toBuilder()
                .displayOrder(displayOrder)
                .updatedAt(now)
                .build();
    }

    private static void validateSpecialType(String code, String name) {
        if (code == null || code.trim().isEmpty()) {
            throw new ValidationException("Special type code is required");
        }
        if (code.length() > 50) {
            throw new ValidationException("Special type code cannot exceed 50 characters");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Special type name is required");
        }
        if (name.length() > 100) {
            throw new ValidationException("Special type name cannot exceed 100 characters");
        }
    }

    private static String normalizeCode(String code) {
        return code.trim().toUpperCase().replace(" ", "_").replace("-", "_");
    }
}

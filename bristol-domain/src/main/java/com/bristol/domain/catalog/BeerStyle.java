package com.bristol.domain.catalog;

import com.bristol.domain.shared.exception.ValidationException;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * BeerStyle aggregate root.
 * Represents a catalog entry for beer styles (IPA, Lager, Stout, etc.).
 * Allows dynamic creation of beer styles without code changes.
 */
@Getter
@Builder(toBuilder = true)
public class BeerStyle {

    private final BeerStyleId id;
    private final String code;              // Unique identifier (e.g., "IPA", "HAZY_IPA")
    private final String name;              // Display name (e.g., "India Pale Ale", "Hazy IPA")
    private final String description;       // Detailed description of the style
    private final BeerStyleCategory category; // Broad category (ALE, LAGER, STOUT, etc.)
    private final boolean active;           // Soft delete flag
    private final Integer displayOrder;     // Order for UI display (lower = first)
    private final byte[] imageData;         // Raw image bytes stored in DB
    private final String imageContentType;  // e.g. "image/png"
    private final String imageFileName;     // Original file name
    private final java.math.BigDecimal abv; // Alcohol by volume (%)
    private final Integer ibu;             // International Bitterness Units
    private final Integer srm;             // Standard Reference Method (color)
    private final Instant createdAt;
    private final Instant updatedAt;

    /**
     * Factory method to create a new beer style.
     */
    public static BeerStyle create(
            String code,
            String name,
            String description,
            BeerStyleCategory category,
            java.math.BigDecimal abv,
            Integer ibu,
            Integer srm,
            Instant now
    ) {
        validateBeerStyle(code, name, category);

        return BeerStyle.builder()
                .id(BeerStyleId.generate())
                .code(normalizeCode(code))
                .name(name.trim())
                .description(description != null ? description.trim() : null)
                .category(category)
                .active(true)
                .displayOrder(0)
                .imageData(null)
                .imageContentType(null)
                .imageFileName(null)
                .abv(abv)
                .ibu(ibu)
                .srm(srm)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Update beer style information.
     */
    public BeerStyle update(
            String name,
            String description,
            BeerStyleCategory category,
            Integer displayOrder,
            java.math.BigDecimal abv,
            Integer ibu,
            Integer srm,
            Instant now
    ) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Beer style name cannot be empty");
        }
        if (category == null) {
            throw new ValidationException("Beer style category is required");
        }

        return this.toBuilder()
                .name(name.trim())
                .description(description != null ? description.trim() : null)
                .category(category)
                .displayOrder(displayOrder != null ? displayOrder : this.displayOrder)
                .abv(abv)
                .ibu(ibu)
                .srm(srm)
                .updatedAt(now)
                .build();
    }

    /**
     * Deactivate (soft delete) the beer style.
     */
    public BeerStyle deactivate(Instant now) {
        return this.toBuilder()
                .active(false)
                .updatedAt(now)
                .build();
    }

    /**
     * Reactivate the beer style.
     */
    public BeerStyle reactivate(Instant now) {
        return this.toBuilder()
                .active(true)
                .updatedAt(now)
                .build();
    }

    public BeerStyle updateImage(byte[] imageData, String contentType, String fileName, Instant now) {
        return this.toBuilder()
                .imageData(imageData)
                .imageContentType(contentType)
                .imageFileName(fileName)
                .updatedAt(now)
                .build();
    }

    public boolean hasImage() {
        return imageData != null && imageData.length > 0;
    }

    /**
     * Update display order for UI sorting.
     */
    public BeerStyle updateDisplayOrder(Integer displayOrder, Instant now) {
        if (displayOrder == null || displayOrder < 0) {
            throw new ValidationException("Display order must be non-negative");
        }

        return this.toBuilder()
                .displayOrder(displayOrder)
                .updatedAt(now)
                .build();
    }

    private static void validateBeerStyle(String code, String name, BeerStyleCategory category) {
        if (code == null || code.trim().isEmpty()) {
            throw new ValidationException("Beer style code is required");
        }
        if (code.length() > 50) {
            throw new ValidationException("Beer style code cannot exceed 50 characters");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Beer style name is required");
        }
        if (name.length() > 100) {
            throw new ValidationException("Beer style name cannot exceed 100 characters");
        }
        if (category == null) {
            throw new ValidationException("Beer style category is required");
        }
    }

    private static String normalizeCode(String code) {
        return code.trim().toUpperCase().replace(" ", "_").replace("-", "_");
    }
}

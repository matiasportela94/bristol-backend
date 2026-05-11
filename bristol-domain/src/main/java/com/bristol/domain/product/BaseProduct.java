package com.bristol.domain.product;

import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.valueobject.Money;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

/**
 * Abstract base class for all product types.
 * Provides common behavior for beer, merch, and special products.
 */
@Getter
@SuperBuilder(toBuilder = true)
public abstract class BaseProduct {

    private final ProductId id;
    private final String name;
    private final String description;
    private final Money basePrice;
    private final Integer stockQuantity;
    private final Integer lowStockThreshold;
    private final boolean featured;
    private final Instant deletedAt;
    private final Instant createdAt;
    private final Instant updatedAt;

    /**
     * Get the kind/classification of product (BEER, MERCH, SPECIAL).
     */
    public abstract ProductKind getProductKind();

    /**
     * Validate product-specific rules.
     */
    protected abstract void validate();

    /**
     * Update stock quantity.
     * Subclasses should override to return their specific type.
     */
    public abstract BaseProduct updateStock(Integer newQuantity, Instant now);

    /**
     * Reduce stock by quantity.
     */
    public BaseProduct reduceStock(Integer quantity, Instant now) {
        if (quantity > stockQuantity) {
            throw new ValidationException("Insufficient stock. Available: " + stockQuantity + ", requested: " + quantity);
        }

        return updateStock(stockQuantity - quantity, now);
    }

    /**
     * Increase stock by quantity.
     */
    public BaseProduct increaseStock(Integer quantity, Instant now) {
        return updateStock(stockQuantity + quantity, now);
    }

    /**
     * Set as featured product.
     */
    public abstract BaseProduct setAsFeatured(Instant now);

    /**
     * Unset as featured product.
     */
    public abstract BaseProduct unsetAsFeatured(Instant now);

    /**
     * Soft delete product.
     */
    public abstract BaseProduct softDelete(Instant now);

    /**
     * Restore soft deleted product.
     */
    public abstract BaseProduct restore(Instant now);

    /**
     * Update base price.
     */
    public abstract BaseProduct updatePrice(Money newPrice, Instant now);

    /**
     * Check if product is in stock.
     */
    public boolean isInStock() {
        return stockQuantity != null && stockQuantity > 0;
    }

    /**
     * Check if stock is low.
     */
    public boolean isLowStock() {
        return stockQuantity != null && lowStockThreshold != null
               && stockQuantity > 0 && stockQuantity <= lowStockThreshold;
    }

    /**
     * Check if product is deleted.
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }

    /**
     * Check if this product kind requires a fixed price.
     * Special products can have null price (requires quote).
     */
    protected boolean requiresFixedPrice() {
        return getProductKind() != ProductKind.SPECIAL;
    }

    /**
     * Validate common product fields.
     */
    protected static void validateCommon(String name, Money basePrice, boolean requiresPrice) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Product name is required");
        }
        if (name.length() > 255) {
            throw new ValidationException("Product name cannot exceed 255 characters");
        }
        if (requiresPrice && (basePrice == null || !basePrice.isPositive())) {
            throw new ValidationException("Product price must be positive");
        }
    }

    // ========== Compatibility methods for legacy code ==========
    // These methods allow polymorphic access to type-specific fields

    /**
     * Get category (mapped from ProductKind).
     * For compatibility with legacy code.
     */
    public ProductCategory getCategory() {
        return switch (getProductKind()) {
            case BEER -> ProductCategory.PRODUCTOS;
            case MERCH -> ProductCategory.MERCHANDISING;
            case SPECIAL -> ProductCategory.ESPECIALES;
        };
    }

    /**
     * Get subcategory (type-specific, returns null for base type).
     * For compatibility with legacy code.
     */
    public ProductSubcategory getSubcategory() {
        return null; // Subclasses override if needed
    }

    /**
     * Get beer type (only for BeerProduct, null otherwise).
     * For compatibility with legacy code.
     */
    public BeerType getBeerType() {
        return null; // BeerProduct overrides
    }

    /**
     * Get ABV (only for BeerProduct, null otherwise).
     * For compatibility with legacy code.
     */
    public java.math.BigDecimal getAbv() {
        return null; // BeerProduct overrides
    }

    /**
     * Get IBU (only for BeerProduct, null otherwise).
     * For compatibility with legacy code.
     */
    public java.math.BigDecimal getIbu() {
        return null; // BeerProduct overrides
    }

    /**
     * Get SRM (only for BeerProduct, null otherwise).
     * For compatibility with legacy code.
     */
    public java.math.BigDecimal getSrm() {
        return null; // BeerProduct overrides
    }

    /**
     * Check if this is a beer product.
     */
    public boolean isBeer() {
        return getProductKind() == ProductKind.BEER;
    }

    /**
     * Check if this is a merchandise product.
     */
    public boolean isMerch() {
        return getProductKind() == ProductKind.MERCH;
    }

    /**
     * Check if this is a special product.
     */
    public boolean isSpecial() {
        return getProductKind() == ProductKind.SPECIAL;
    }
}

package com.bristol.domain.product;

import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.valueobject.Money;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * Product aggregate root.
 * Represents a product in the catalog (beer, merchandising, or special items).
 */
@Getter
@Builder(toBuilder = true)
public class Product {

    private final ProductId id;
    private final String name;
    private final String description;
    private final ProductCategory category;
    private final ProductSubcategory subcategory;
    private final BeerType beerType;
    private final Money basePrice;
    private final Integer stockQuantity;
    private final Integer lowStockThreshold;
    private final boolean featured;
    private final Instant deletedAt;
    private final Instant createdAt;
    private final Instant updatedAt;

    /**
     * Factory method to create a new product.
     */
    public static Product create(
            String name,
            String description,
            ProductCategory category,
            ProductSubcategory subcategory,
            BeerType beerType,
            Money basePrice,
            Integer stockQuantity,
            Integer lowStockThreshold,
            Instant now
    ) {
        validateProduct(name, category, basePrice);
        validateBeerType(category, beerType);

        return Product.builder()
                .id(ProductId.generate())
                .name(name)
                .description(description)
                .category(category)
                .subcategory(subcategory)
                .beerType(beerType)
                .basePrice(basePrice)
                .stockQuantity(stockQuantity != null ? stockQuantity : 0)
                .lowStockThreshold(lowStockThreshold != null ? lowStockThreshold : 10)
                .featured(false)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Update product information.
     */
    public Product update(
            String name,
            String description,
            ProductCategory category,
            ProductSubcategory subcategory,
            BeerType beerType,
            Money basePrice,
            Instant now
    ) {
        validateProduct(name, category, basePrice);
        validateBeerType(category, beerType);

        return this.toBuilder()
                .name(name)
                .description(description)
                .category(category)
                .subcategory(subcategory)
                .beerType(beerType)
                .basePrice(basePrice)
                .updatedAt(now)
                .build();
    }

    /**
     * Update stock quantity.
     */
    public Product updateStock(Integer newQuantity, Instant now) {
        if (newQuantity < 0) {
            throw new ValidationException("Stock quantity cannot be negative");
        }

        return this.toBuilder()
                .stockQuantity(newQuantity)
                .updatedAt(now)
                .build();
    }

    /**
     * Reduce stock by quantity.
     */
    public Product reduceStock(Integer quantity, Instant now) {
        if (quantity > stockQuantity) {
            throw new ValidationException("Insufficient stock. Available: " + stockQuantity + ", requested: " + quantity);
        }

        return updateStock(stockQuantity - quantity, now);
    }

    /**
     * Increase stock by quantity.
     */
    public Product increaseStock(Integer quantity, Instant now) {
        return updateStock(stockQuantity + quantity, now);
    }

    /**
     * Set as featured product.
     */
    public Product setAsFeatured(Instant now) {
        return this.toBuilder()
                .featured(true)
                .updatedAt(now)
                .build();
    }

    /**
     * Unset as featured product.
     */
    public Product unsetAsFeatured(Instant now) {
        return this.toBuilder()
                .featured(false)
                .updatedAt(now)
                .build();
    }

    /**
     * Soft delete product.
     */
    public Product softDelete(Instant now) {
        return this.toBuilder()
                .deletedAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Restore soft deleted product.
     */
    public Product restore(Instant now) {
        return this.toBuilder()
                .deletedAt(null)
                .updatedAt(now)
                .build();
    }

    /**
     * Update base price.
     */
    public Product updatePrice(Money newPrice, Instant now) {
        if (requiresFixedPrice(category) && (newPrice == null || !newPrice.isPositive())) {
            throw new ValidationException("Price must be positive");
        }

        return this.toBuilder()
                .basePrice(newPrice)
                .updatedAt(now)
                .build();
    }

    /**
     * Check if product is in stock.
     */
    public boolean isInStock() {
        return stockQuantity > 0;
    }

    /**
     * Check if stock is low.
     */
    public boolean isLowStock() {
        return stockQuantity > 0 && stockQuantity <= lowStockThreshold;
    }

    /**
     * Check if product is deleted.
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }

    /**
     * Check if product is a beer.
     */
    public boolean isBeer() {
        return category == ProductCategory.PRODUCTOS;
    }

    private static void validateProduct(String name, ProductCategory category, Money basePrice) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Product name is required");
        }
        if (name.length() > 255) {
            throw new ValidationException("Product name cannot exceed 255 characters");
        }
        if (category == null) {
            throw new ValidationException("Product category is required");
        }
        if (requiresFixedPrice(category) && (basePrice == null || !basePrice.isPositive())) {
            throw new ValidationException("Product price must be positive");
        }
    }

    private static void validateBeerType(ProductCategory category, BeerType beerType) {
        if (category == ProductCategory.PRODUCTOS && beerType == null) {
            throw new ValidationException("Beer type is required for beer products");
        }
    }

    private static boolean requiresFixedPrice(ProductCategory category) {
        return category != ProductCategory.ESPECIALES;
    }
}

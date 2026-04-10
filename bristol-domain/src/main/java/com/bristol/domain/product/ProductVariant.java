package com.bristol.domain.product;

import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.valueobject.Money;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * ProductVariant entity.
 * Represents a variant of a product (e.g., different sizes or colors).
 */
@Getter
@Builder(toBuilder = true)
public class ProductVariant {

    private final ProductVariantId id;
    private final ProductId productId;
    private final String sku;
    private final String size;
    private final Integer sizeMl;
    private final String color;
    private final Money additionalPrice;
    private final Integer stockQuantity;
    private final String imageUrl;
    private final Instant createdAt;
    private final Instant updatedAt;

    /**
     * Factory method to create a new product variant.
     */
    public static ProductVariant create(
            ProductId productId,
            String sku,
            String size,
            String color,
            Money additionalPrice,
            Integer stockQuantity,
            String imageUrl,
            Instant now
    ) {
        if (sku != null && sku.trim().isEmpty()) {
            throw new ValidationException("SKU cannot be empty");
        }

        return ProductVariant.builder()
                .id(ProductVariantId.generate())
                .productId(productId)
                .sku(sku)
                .size(size)
                .sizeMl(parseSizeMl(size))
                .color(color)
                .additionalPrice(additionalPrice != null ? additionalPrice : Money.zero())
                .stockQuantity(stockQuantity != null ? stockQuantity : 0)
                .imageUrl(imageUrl)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Update stock quantity.
     */
    public ProductVariant updateStock(Integer newQuantity, Instant now) {
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
    public ProductVariant reduceStock(Integer quantity, Instant now) {
        if (quantity > stockQuantity) {
            throw new ValidationException("Insufficient stock");
        }

        return updateStock(stockQuantity - quantity, now);
    }

    /**
     * Check if variant is in stock.
     */
    public boolean isInStock() {
        return stockQuantity > 0;
    }

    /**
     * Get full variant description.
     */
    public String getDescription() {
        StringBuilder desc = new StringBuilder();
        if (size != null) {
            desc.append("Size: ").append(size);
        }
        if (color != null) {
            if (desc.length() > 0) desc.append(", ");
            desc.append("Color: ").append(color);
        }
        return desc.toString();
    }

    private static Integer parseSizeMl(String size) {
        if (size == null || size.isBlank()) {
            return null;
        }

        String digitsOnly = size.replaceAll("[^0-9]", "");
        if (digitsOnly.isBlank()) {
            return null;
        }

        try {
            return Integer.parseInt(digitsOnly);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}

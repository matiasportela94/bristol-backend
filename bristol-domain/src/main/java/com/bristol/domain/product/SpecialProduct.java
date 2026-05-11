package com.bristol.domain.product;

import com.bristol.domain.catalog.SpecialTypeId;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.valueobject.Money;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

/**
 * Special product aggregate.
 * Represents special products like ploteos, events, catering, etc.
 * These products may require price quotation.
 */
@Getter
@SuperBuilder(toBuilder = true)
public class SpecialProduct extends BaseProduct {

    private final SpecialTypeId specialTypeId;  // FK to special_types catalog
    private final String notes;                  // Additional notes/description
    private final boolean requiresQuote;         // true = price is quoted per customer

    @Override
    public ProductKind getProductKind() {
        return ProductKind.SPECIAL;
    }

    @Override
    protected void validate() {
        if (specialTypeId == null) {
            throw new ValidationException("Special products must have a special type");
        }
        if (requiresQuote && getBasePrice() != null) {
            throw new ValidationException("Products requiring quote cannot have fixed price");
        }
        if (!requiresQuote && (getBasePrice() == null || !getBasePrice().isPositive())) {
            throw new ValidationException("Products not requiring quote must have a positive price");
        }
    }

    @Override
    protected boolean requiresFixedPrice() {
        return !requiresQuote;
    }

    /**
     * Factory method to create a new special product.
     */
    public static SpecialProduct create(
            String name,
            String description,
            Money basePrice,
            SpecialTypeId specialTypeId,
            String notes,
            boolean requiresQuote,
            Integer stockQuantity,
            Integer lowStockThreshold,
            Instant now
    ) {
        validateCommon(name, basePrice, !requiresQuote);

        SpecialProduct product = SpecialProduct.builder()
                .id(ProductId.generate())
                .name(name)
                .description(description)
                .basePrice(basePrice)
                .specialTypeId(specialTypeId)
                .notes(notes)
                .requiresQuote(requiresQuote)
                .stockQuantity(stockQuantity != null ? stockQuantity : 0)
                .lowStockThreshold(lowStockThreshold != null ? lowStockThreshold : 10)
                .featured(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        product.validate();
        return product;
    }

    @Override
    public SpecialProduct updateStock(Integer newQuantity, Instant now) {
        if (newQuantity < 0) {
            throw new ValidationException("Stock quantity cannot be negative");
        }
        return this.toBuilder().stockQuantity(newQuantity).updatedAt(now).build();
    }

    @Override
    public SpecialProduct setAsFeatured(Instant now) {
        return this.toBuilder().featured(true).updatedAt(now).build();
    }

    @Override
    public SpecialProduct unsetAsFeatured(Instant now) {
        return this.toBuilder().featured(false).updatedAt(now).build();
    }

    @Override
    public SpecialProduct softDelete(Instant now) {
        return this.toBuilder().deletedAt(now).updatedAt(now).build();
    }

    @Override
    public SpecialProduct restore(Instant now) {
        return this.toBuilder().deletedAt(null).updatedAt(now).build();
    }

    @Override
    public SpecialProduct updatePrice(Money newPrice, Instant now) {
        if (requiresFixedPrice() && (newPrice == null || !newPrice.isPositive())) {
            throw new ValidationException("Price must be positive");
        }
        return this.toBuilder().basePrice(newPrice).updatedAt(now).build();
    }

    /**
     * Update special product information.
     */
    public SpecialProduct update(
            String name,
            String description,
            Money basePrice,
            SpecialTypeId specialTypeId,
            String notes,
            boolean requiresQuote,
            Instant now
    ) {
        validateCommon(name, basePrice, !requiresQuote);

        SpecialProduct updated = this.toBuilder()
                .name(name)
                .description(description)
                .basePrice(basePrice)
                .specialTypeId(specialTypeId)
                .notes(notes)
                .requiresQuote(requiresQuote)
                .updatedAt(now)
                .build();

        updated.validate();
        return updated;
    }
}

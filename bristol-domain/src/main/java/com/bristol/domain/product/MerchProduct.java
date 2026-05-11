package com.bristol.domain.product;

import com.bristol.domain.catalog.MerchCategory;
import com.bristol.domain.catalog.MerchTypeId;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.valueobject.Money;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

/**
 * Merch product aggregate.
 * Represents merchandise products like shirts, hoodies, glasses, etc.
 */
@Getter
@SuperBuilder(toBuilder = true)
public class MerchProduct extends BaseProduct {

    private final MerchTypeId merchTypeId;      // FK to merch_types catalog
    private final MerchCategory merchCategory;  // Denormalized for quick queries
    private final String material;              // e.g., "Algodón", "Poliéster", "Vidrio"
    private final String brand;                 // e.g., "Bristol", "Generic"

    @Override
    public ProductKind getProductKind() {
        return ProductKind.MERCH;
    }

    @Override
    protected void validate() {
        if (merchTypeId == null) {
            throw new ValidationException("Merch products must have a merch type");
        }
    }

    /**
     * Factory method to create a new merch product.
     */
    public static MerchProduct create(
            String name,
            String description,
            Money basePrice,
            MerchTypeId merchTypeId,
            MerchCategory merchCategory,
            String material,
            String brand,
            Integer stockQuantity,
            Integer lowStockThreshold,
            Instant now
    ) {
        validateCommon(name, basePrice, true);

        MerchProduct product = MerchProduct.builder()
                .id(ProductId.generate())
                .name(name)
                .description(description)
                .basePrice(basePrice)
                .merchTypeId(merchTypeId)
                .merchCategory(merchCategory)
                .material(material)
                .brand(brand)
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
    public MerchProduct updateStock(Integer newQuantity, Instant now) {
        if (newQuantity < 0) {
            throw new ValidationException("Stock quantity cannot be negative");
        }
        return this.toBuilder().stockQuantity(newQuantity).updatedAt(now).build();
    }

    @Override
    public MerchProduct setAsFeatured(Instant now) {
        return this.toBuilder().featured(true).updatedAt(now).build();
    }

    @Override
    public MerchProduct unsetAsFeatured(Instant now) {
        return this.toBuilder().featured(false).updatedAt(now).build();
    }

    @Override
    public MerchProduct softDelete(Instant now) {
        return this.toBuilder().deletedAt(now).updatedAt(now).build();
    }

    @Override
    public MerchProduct restore(Instant now) {
        return this.toBuilder().deletedAt(null).updatedAt(now).build();
    }

    @Override
    public MerchProduct updatePrice(Money newPrice, Instant now) {
        if (requiresFixedPrice() && (newPrice == null || !newPrice.isPositive())) {
            throw new ValidationException("Price must be positive");
        }
        return this.toBuilder().basePrice(newPrice).updatedAt(now).build();
    }

    /**
     * Update merch product information.
     */
    public MerchProduct update(
            String name,
            String description,
            Money basePrice,
            MerchTypeId merchTypeId,
            MerchCategory merchCategory,
            String material,
            String brand,
            Instant now
    ) {
        validateCommon(name, basePrice, true);

        MerchProduct updated = this.toBuilder()
                .name(name)
                .description(description)
                .basePrice(basePrice)
                .merchTypeId(merchTypeId)
                .merchCategory(merchCategory)
                .material(material)
                .brand(brand)
                .updatedAt(now)
                .build();

        updated.validate();
        return updated;
    }

    // ========== Compatibility overrides for legacy code ==========

    @Override
    public ProductSubcategory getSubcategory() {
        if (merchCategory == null) return ProductSubcategory.OTRO;

        return switch (merchCategory) {
            case CLOTHING -> ProductSubcategory.REMERA;
            case GLASSWARE -> ProductSubcategory.VASO;
            case ACCESSORIES -> ProductSubcategory.GORRA;
            case OTHER -> ProductSubcategory.OTRO;
        };
    }
}

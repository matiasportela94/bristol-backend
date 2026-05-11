package com.bristol.domain.product;

import com.bristol.domain.catalog.BeerStyleCategory;
import com.bristol.domain.catalog.BeerStyleId;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.valueobject.Money;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Beer product aggregate.
 * Represents beer products with specific attributes like ABV, IBU, etc.
 */
@Getter
@SuperBuilder(toBuilder = true)
public class BeerProduct extends BaseProduct {

    private final BeerStyleId beerStyleId;        // FK to beer_styles catalog
    private final BeerStyleCategory beerCategory;  // Denormalized for quick queries
    private final BigDecimal abv;                  // Alcohol by volume
    private final Integer ibu;                     // International Bitterness Units
    private final Integer srm;                     // Standard Reference Method (color)
    private final String origin;                   // e.g., "Bristol Brewery", "Importada"
    private final String brewery;                  // Brewery name

    @Override
    public ProductKind getProductKind() {
        return ProductKind.BEER;
    }

    @Override
    protected void validate() {
        if (beerStyleId == null) {
            throw new ValidationException("Beer products must have a beer style");
        }
        if (abv != null && abv.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("ABV must be positive");
        }
        if (ibu != null && ibu < 0) {
            throw new ValidationException("IBU cannot be negative");
        }
        if (srm != null && srm < 0) {
            throw new ValidationException("SRM cannot be negative");
        }
    }

    /**
     * Factory method to create a new beer product.
     */
    public static BeerProduct create(
            String name,
            String description,
            Money basePrice,
            BeerStyleId beerStyleId,
            BeerStyleCategory beerCategory,
            BigDecimal abv,
            Integer ibu,
            Integer srm,
            String origin,
            String brewery,
            Integer stockQuantity,
            Integer lowStockThreshold,
            Instant now
    ) {
        validateCommon(name, basePrice, true);

        BeerProduct product = BeerProduct.builder()
                .id(ProductId.generate())
                .name(name)
                .description(description)
                .basePrice(basePrice)
                .beerStyleId(beerStyleId)
                .beerCategory(beerCategory)
                .abv(abv)
                .ibu(ibu)
                .srm(srm)
                .origin(origin)
                .brewery(brewery)
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
    public BeerProduct updateStock(Integer newQuantity, Instant now) {
        if (newQuantity < 0) {
            throw new ValidationException("Stock quantity cannot be negative");
        }
        return this.toBuilder()
                .stockQuantity(newQuantity)
                .updatedAt(now)
                .build();
    }

    @Override
    public BeerProduct setAsFeatured(Instant now) {
        return this.toBuilder().featured(true).updatedAt(now).build();
    }

    @Override
    public BeerProduct unsetAsFeatured(Instant now) {
        return this.toBuilder().featured(false).updatedAt(now).build();
    }

    @Override
    public BeerProduct softDelete(Instant now) {
        return this.toBuilder().deletedAt(now).updatedAt(now).build();
    }

    @Override
    public BeerProduct restore(Instant now) {
        return this.toBuilder().deletedAt(null).updatedAt(now).build();
    }

    @Override
    public BeerProduct updatePrice(Money newPrice, Instant now) {
        if (requiresFixedPrice() && (newPrice == null || !newPrice.isPositive())) {
            throw new ValidationException("Price must be positive");
        }
        return this.toBuilder().basePrice(newPrice).updatedAt(now).build();
    }

    /**
     * Update beer product information.
     */
    public BeerProduct update(
            String name,
            String description,
            Money basePrice,
            BeerStyleId beerStyleId,
            BeerStyleCategory beerCategory,
            BigDecimal abv,
            Integer ibu,
            Integer srm,
            String origin,
            String brewery,
            Instant now
    ) {
        validateCommon(name, basePrice, true);

        BeerProduct updated = this.toBuilder()
                .name(name)
                .description(description)
                .basePrice(basePrice)
                .beerStyleId(beerStyleId)
                .beerCategory(beerCategory)
                .abv(abv)
                .ibu(ibu)
                .srm(srm)
                .origin(origin)
                .brewery(brewery)
                .updatedAt(now)
                .build();

        updated.validate();
        return updated;
    }

    // ========== Compatibility overrides for legacy code ==========

    @Override
    public BeerType getBeerType() {
        if (beerCategory == null) return null;

        return switch (beerCategory) {
            case ALE -> BeerType.APA;
            case LAGER -> BeerType.LAGER;
            case STOUT -> BeerType.STOUT;
            case WHEAT -> BeerType.WHEAT;
            case SOUR -> BeerType.SOUR;
            case SPECIALTY -> BeerType.OTRO;
        };
    }

    @Override
    public BigDecimal getAbv() {
        return abv;
    }

    @Override
    public BigDecimal getIbu() {
        return ibu != null ? BigDecimal.valueOf(ibu) : null;
    }

    @Override
    public BigDecimal getSrm() {
        return srm != null ? BigDecimal.valueOf(srm) : null;
    }

    @Override
    public ProductSubcategory getSubcategory() {
        // Default beer subcategory - can be enhanced based on packaging type
        return ProductSubcategory.CAN;
    }
}

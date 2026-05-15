package com.bristol.domain.product;

import com.bristol.domain.catalog.BeerStyleId;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.valueobject.Money;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@SuperBuilder(toBuilder = true)
public class BeerProduct extends BaseProduct {

    private final BeerStyleId beerStyleId;        // FK to beer_styles catalog
    private final String origin;
    private final String brewery;
    private final Integer cansPerUnit;             // How many cans this product contains (1=single, 6=six-pack, 24=case, etc.)

    @Override
    public ProductKind getProductKind() {
        return ProductKind.BEER;
    }

    @Override
    protected void validate() {
        if (beerStyleId == null) {
            throw new ValidationException("Beer products must have a beer style");
        }
    }

    public static BeerProduct create(
            String name,
            String description,
            Money basePrice,
            BeerStyleId beerStyleId,
            String origin,
            String brewery,
            Integer stockQuantity,
            Integer lowStockThreshold,
            Integer cansPerUnit,
            Instant now
    ) {
        validateCommon(name, basePrice, true);

        BeerProduct product = BeerProduct.builder()
                .id(ProductId.generate())
                .name(name)
                .description(description)
                .basePrice(basePrice)
                .beerStyleId(beerStyleId)
                .origin(origin)
                .brewery(brewery)
                .stockQuantity(stockQuantity != null ? stockQuantity : 0)
                .lowStockThreshold(lowStockThreshold != null ? lowStockThreshold : 10)
                .cansPerUnit(cansPerUnit != null ? cansPerUnit : 1)
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

    public BeerProduct update(
            String name,
            String description,
            Money basePrice,
            BeerStyleId beerStyleId,
            String origin,
            String brewery,
            Integer cansPerUnit,
            Instant now
    ) {
        validateCommon(name, basePrice, true);

        BeerProduct updated = this.toBuilder()
                .name(name)
                .description(description)
                .basePrice(basePrice)
                .beerStyleId(beerStyleId)
                .origin(origin)
                .brewery(brewery)
                .cansPerUnit(cansPerUnit != null ? cansPerUnit : this.cansPerUnit)
                .updatedAt(now)
                .build();

        updated.validate();
        return updated;
    }

    @Override
    public ProductSubcategory getSubcategory() {
        return ProductSubcategory.CAN;
    }
}

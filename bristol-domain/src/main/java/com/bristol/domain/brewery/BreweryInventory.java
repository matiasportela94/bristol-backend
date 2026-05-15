package com.bristol.domain.brewery;

import com.bristol.domain.catalog.BeerStyleId;
import com.bristol.domain.shared.exception.ValidationException;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder(toBuilder = true)
public class BreweryInventory {

    private final BreweryInventoryId id;
    private final BeerStyleId beerStyleId;
    private final int totalCans;
    private final Instant createdAt;
    private final Instant updatedAt;

    public static BreweryInventory create(BeerStyleId beerStyleId, Instant now) {
        if (beerStyleId == null) {
            throw new ValidationException("Beer style is required for brewery inventory");
        }
        return BreweryInventory.builder()
                .id(BreweryInventoryId.generate())
                .beerStyleId(beerStyleId)
                .totalCans(0)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public BreweryInventory addCans(int cans, Instant now) {
        if (cans <= 0) {
            throw new ValidationException("Amount of cans to add must be positive");
        }
        return this.toBuilder()
                .totalCans(this.totalCans + cans)
                .updatedAt(now)
                .build();
    }

    public BreweryInventory deductCans(int cans, Instant now) {
        if (cans <= 0) {
            throw new ValidationException("Amount of cans to deduct must be positive");
        }
        if (this.totalCans < cans) {
            throw new ValidationException(
                    "Insufficient can inventory. Available: " + this.totalCans + ", Required: " + cans);
        }
        return this.toBuilder()
                .totalCans(this.totalCans - cans)
                .updatedAt(now)
                .build();
    }

    public BreweryInventory adjustTo(int newTotal, Instant now) {
        if (newTotal < 0) {
            throw new ValidationException("Total cans cannot be negative");
        }
        return this.toBuilder()
                .totalCans(newTotal)
                .updatedAt(now)
                .build();
    }

    public boolean hasEnoughCans(int required) {
        return this.totalCans >= required;
    }
}

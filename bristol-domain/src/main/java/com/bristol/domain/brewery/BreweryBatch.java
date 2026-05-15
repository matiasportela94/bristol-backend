package com.bristol.domain.brewery;

import com.bristol.domain.catalog.BeerStyleId;
import com.bristol.domain.shared.exception.ValidationException;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Builder(toBuilder = true)
public class BreweryBatch {

    private final BreweryBatchId id;
    private final BeerStyleId beerStyleId;
    private final int cansProduced;
    private final Integer canCapacityMl;
    private final BigDecimal costPerCan;
    private final String notes;
    private final Instant createdAt;

    public static BreweryBatch create(BeerStyleId beerStyleId, int cansProduced, Integer canCapacityMl, BigDecimal costPerCan, String notes, Instant now) {
        if (beerStyleId == null) {
            throw new ValidationException("Beer style is required for a brewing batch");
        }
        if (cansProduced <= 0) {
            throw new ValidationException("Cans produced must be positive");
        }
        if (canCapacityMl != null && canCapacityMl <= 0) {
            throw new ValidationException("Can capacity must be positive");
        }
        if (costPerCan != null && costPerCan.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Cost per can cannot be negative");
        }
        return BreweryBatch.builder()
                .id(BreweryBatchId.generate())
                .beerStyleId(beerStyleId)
                .cansProduced(cansProduced)
                .canCapacityMl(canCapacityMl)
                .costPerCan(costPerCan)
                .notes(notes != null ? notes.trim() : null)
                .createdAt(now)
                .build();
    }
}

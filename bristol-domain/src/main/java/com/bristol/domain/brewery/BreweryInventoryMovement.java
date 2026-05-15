package com.bristol.domain.brewery;

import com.bristol.domain.catalog.BeerStyleId;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class BreweryInventoryMovement {

    private final BreweryInventoryMovementId id;
    private final BeerStyleId beerStyleId;
    private final MovementType type;
    private final int cansDelta;       // positive = stock in, negative = stock out
    private final int cansBefore;
    private final int cansAfter;
    private final UUID referenceId;    // orderId or batchId — nullable
    private final String referenceType;// "ORDER" | "BATCH" — nullable
    private final String notes;
    private final Instant createdAt;

    public static BreweryInventoryMovement create(
            BeerStyleId beerStyleId,
            MovementType type,
            int cansDelta,
            int cansBefore,
            int cansAfter,
            UUID referenceId,
            String referenceType,
            String notes,
            Instant now
    ) {
        return BreweryInventoryMovement.builder()
                .id(BreweryInventoryMovementId.generate())
                .beerStyleId(beerStyleId)
                .type(type)
                .cansDelta(cansDelta)
                .cansBefore(cansBefore)
                .cansAfter(cansAfter)
                .referenceId(referenceId)
                .referenceType(referenceType)
                .notes(notes)
                .createdAt(now)
                .build();
    }
}

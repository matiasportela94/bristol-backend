package com.bristol.domain.brewery;

import java.util.List;

public interface BreweryInventoryMovementRepository {
    BreweryInventoryMovement save(BreweryInventoryMovement movement);
    List<BreweryInventoryMovement> findByBeerStyleId(com.bristol.domain.catalog.BeerStyleId beerStyleId);
    List<BreweryInventoryMovement> findAll();
}

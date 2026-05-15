package com.bristol.domain.brewery;

import com.bristol.domain.catalog.BeerStyleId;

import java.util.List;
import java.util.Optional;

public interface BreweryInventoryRepository {
    BreweryInventory save(BreweryInventory inventory);
    Optional<BreweryInventory> findById(BreweryInventoryId id);
    Optional<BreweryInventory> findByBeerStyleId(BeerStyleId beerStyleId);
    List<BreweryInventory> findAll();
    boolean existsByBeerStyleId(BeerStyleId beerStyleId);
}

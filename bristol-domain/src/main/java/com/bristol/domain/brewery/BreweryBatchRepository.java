package com.bristol.domain.brewery;

import com.bristol.domain.catalog.BeerStyleId;

import java.util.List;

public interface BreweryBatchRepository {
    BreweryBatch save(BreweryBatch batch);
    List<BreweryBatch> findAll();
    List<BreweryBatch> findByBeerStyleId(BeerStyleId beerStyleId);
}

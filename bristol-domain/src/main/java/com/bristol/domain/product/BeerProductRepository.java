package com.bristol.domain.product;

import com.bristol.domain.catalog.BeerStyleCategory;
import com.bristol.domain.catalog.BeerStyleId;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for BeerProduct aggregate.
 */
public interface BeerProductRepository {

    BeerProduct save(BeerProduct product);

    Optional<BeerProduct> findById(ProductId id);

    List<BeerProduct> findAll();

    List<BeerProduct> findByBeerStyle(BeerStyleId beerStyleId);

    List<BeerProduct> findByCategory(BeerStyleCategory category);

    List<BeerProduct> findFeatured();

    List<BeerProduct> findInStock();

    List<BeerProduct> findLowStock();

    List<BeerProduct> searchByName(String name);

    List<BeerProduct> findByBrewery(String brewery);

    void delete(ProductId id);
}

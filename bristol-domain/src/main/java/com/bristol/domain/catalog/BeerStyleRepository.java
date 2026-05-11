package com.bristol.domain.catalog;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for BeerStyle aggregate.
 */
public interface BeerStyleRepository {

    BeerStyle save(BeerStyle beerStyle);

    Optional<BeerStyle> findById(BeerStyleId id);

    Optional<BeerStyle> findByCode(String code);

    List<BeerStyle> findAll();

    List<BeerStyle> findByCategory(BeerStyleCategory category);

    List<BeerStyle> findActive();

    boolean existsByCode(String code);

    void delete(BeerStyleId id);
}

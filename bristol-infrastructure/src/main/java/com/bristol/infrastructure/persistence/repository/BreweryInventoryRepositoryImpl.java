package com.bristol.infrastructure.persistence.repository;

import com.bristol.domain.brewery.BreweryInventory;
import com.bristol.domain.brewery.BreweryInventoryId;
import com.bristol.domain.brewery.BreweryInventoryRepository;
import com.bristol.domain.catalog.BeerStyleId;
import com.bristol.infrastructure.persistence.mapper.BreweryInventoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BreweryInventoryRepositoryImpl implements BreweryInventoryRepository {

    private final JpaBreweryInventoryRepository jpaRepository;
    private final BreweryInventoryMapper mapper;

    @Override
    public BreweryInventory save(BreweryInventory inventory) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(inventory)));
    }

    @Override
    public Optional<BreweryInventory> findById(BreweryInventoryId id) {
        return jpaRepository.findById(id.getValue()).map(mapper::toDomain);
    }

    @Override
    public Optional<BreweryInventory> findByBeerStyleId(BeerStyleId beerStyleId) {
        return jpaRepository.findByBeerStyleId(beerStyleId.getValue()).map(mapper::toDomain);
    }

    @Override
    public List<BreweryInventory> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByBeerStyleId(BeerStyleId beerStyleId) {
        return jpaRepository.existsByBeerStyleId(beerStyleId.getValue());
    }
}

package com.bristol.infrastructure.persistence.repository;

import com.bristol.domain.brewery.BreweryInventoryMovement;
import com.bristol.domain.brewery.BreweryInventoryMovementRepository;
import com.bristol.domain.catalog.BeerStyleId;
import com.bristol.infrastructure.persistence.mapper.BreweryInventoryMovementMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BreweryInventoryMovementRepositoryImpl implements BreweryInventoryMovementRepository {

    private final JpaBreweryInventoryMovementRepository jpaRepository;
    private final BreweryInventoryMovementMapper mapper;

    @Override
    public BreweryInventoryMovement save(BreweryInventoryMovement movement) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(movement)));
    }

    @Override
    public List<BreweryInventoryMovement> findByBeerStyleId(BeerStyleId beerStyleId) {
        return jpaRepository.findByBeerStyleIdOrderByCreatedAtDesc(beerStyleId.getValue())
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<BreweryInventoryMovement> findAll() {
        return jpaRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(mapper::toDomain).toList();
    }
}

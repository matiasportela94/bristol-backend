package com.bristol.infrastructure.persistence.repository;

import com.bristol.infrastructure.persistence.entity.BreweryInventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaBreweryInventoryRepository extends JpaRepository<BreweryInventoryEntity, UUID> {
    Optional<BreweryInventoryEntity> findByBeerStyleId(UUID beerStyleId);
    boolean existsByBeerStyleId(UUID beerStyleId);
}

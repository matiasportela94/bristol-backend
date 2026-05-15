package com.bristol.infrastructure.persistence.repository;

import com.bristol.infrastructure.persistence.entity.BreweryInventoryMovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaBreweryInventoryMovementRepository extends JpaRepository<BreweryInventoryMovementEntity, UUID> {
    List<BreweryInventoryMovementEntity> findByBeerStyleIdOrderByCreatedAtDesc(UUID beerStyleId);
    List<BreweryInventoryMovementEntity> findAllByOrderByCreatedAtDesc();
}

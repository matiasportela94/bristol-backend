package com.bristol.infrastructure.persistence.repository;

import com.bristol.infrastructure.persistence.entity.BreweryBatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaBreweryBatchRepository extends JpaRepository<BreweryBatchEntity, UUID> {
    List<BreweryBatchEntity> findByBeerStyleIdOrderByCreatedAtDesc(UUID beerStyleId);
    List<BreweryBatchEntity> findAllByOrderByCreatedAtDesc();
}

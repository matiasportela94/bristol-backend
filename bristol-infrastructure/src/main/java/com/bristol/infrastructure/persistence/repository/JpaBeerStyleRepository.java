package com.bristol.infrastructure.persistence.repository;

import com.bristol.infrastructure.persistence.entity.BeerStyleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for BeerStyleEntity.
 */
@Repository
public interface JpaBeerStyleRepository extends JpaRepository<BeerStyleEntity, UUID> {

    Optional<BeerStyleEntity> findByCode(String code);

    List<BeerStyleEntity> findByCategory(BeerStyleEntity.BeerStyleCategoryEnum category);

    List<BeerStyleEntity> findByActiveTrue();

    List<BeerStyleEntity> findAllByOrderByDisplayOrderAsc();

    boolean existsByCode(String code);
}

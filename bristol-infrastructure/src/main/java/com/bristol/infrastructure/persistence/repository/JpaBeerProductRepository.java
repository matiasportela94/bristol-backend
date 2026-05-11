package com.bristol.infrastructure.persistence.repository;

import com.bristol.infrastructure.persistence.entity.BeerProductEntity;
import com.bristol.infrastructure.persistence.entity.BeerProductEntity.BeerCategoryEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for BeerProductEntity.
 */
@Repository
public interface JpaBeerProductRepository extends JpaRepository<BeerProductEntity, UUID> {

    @Query("SELECT b FROM BeerProductEntity b WHERE b.deletedAt IS NULL")
    List<BeerProductEntity> findAllActive();

    @Query("SELECT b FROM BeerProductEntity b WHERE b.beerStyleId = :beerStyleId AND b.deletedAt IS NULL")
    List<BeerProductEntity> findByBeerStyleId(@Param("beerStyleId") UUID beerStyleId);

    @Query("SELECT b FROM BeerProductEntity b WHERE b.beerCategory = :category AND b.deletedAt IS NULL")
    List<BeerProductEntity> findByBeerCategory(@Param("category") BeerCategoryEnum category);

    @Query("SELECT b FROM BeerProductEntity b WHERE b.isFeatured = true AND b.deletedAt IS NULL")
    List<BeerProductEntity> findFeatured();

    @Query("SELECT b FROM BeerProductEntity b WHERE b.stockQuantity > 0 AND b.deletedAt IS NULL")
    List<BeerProductEntity> findInStock();

    @Query("SELECT b FROM BeerProductEntity b WHERE b.stockQuantity <= b.lowStockThreshold AND b.stockQuantity > 0 AND b.deletedAt IS NULL")
    List<BeerProductEntity> findLowStock();

    @Query("SELECT b FROM BeerProductEntity b WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%')) AND b.deletedAt IS NULL")
    List<BeerProductEntity> searchByName(@Param("name") String name);

    @Query("SELECT b FROM BeerProductEntity b WHERE LOWER(b.brewery) = LOWER(:brewery) AND b.deletedAt IS NULL")
    List<BeerProductEntity> findByBrewery(@Param("brewery") String brewery);
}

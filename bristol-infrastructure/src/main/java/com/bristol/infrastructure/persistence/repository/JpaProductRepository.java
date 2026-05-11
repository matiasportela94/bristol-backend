package com.bristol.infrastructure.persistence.repository;

import com.bristol.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for ProductEntity.
 */
@Repository
public interface JpaProductRepository extends JpaRepository<ProductEntity, UUID> {

    List<ProductEntity> findByIsActiveTrueAndDeletedAtIsNull();

    List<ProductEntity> findByCategoryAndIsActiveTrueAndDeletedAtIsNull(ProductEntity.ProductCategoryEnum category);

    @Query("SELECT p FROM ProductEntity p WHERE p.isActive = true AND p.deletedAt IS NULL " +
           "AND p.stockQuantity > 0")
    List<ProductEntity> findInStock();

    @Query("SELECT p FROM ProductEntity p WHERE p.isActive = true AND p.deletedAt IS NULL " +
           "AND p.stockQuantity <= p.lowStockThreshold")
    List<ProductEntity> findLowStock();

    List<ProductEntity> findByBeerTypeAndIsActiveTrueAndDeletedAtIsNull(ProductEntity.BeerTypeEnum beerType);

    @Query("SELECT p FROM ProductEntity p WHERE p.isActive = true AND p.deletedAt IS NULL " +
           "AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<ProductEntity> searchByName(String name);

    // Paginated queries
    @Query("SELECT p FROM ProductEntity p WHERE p.isActive = true AND p.deletedAt IS NULL")
    Page<ProductEntity> findAllPaginated(Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE p.category = :category " +
           "AND p.isActive = true AND p.deletedAt IS NULL")
    Page<ProductEntity> findByCategoryPaginated(ProductEntity.ProductCategoryEnum category, Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE p.isFeatured = true " +
           "AND p.isActive = true AND p.deletedAt IS NULL")
    Page<ProductEntity> findFeaturedPaginated(Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE p.isActive = true AND p.deletedAt IS NULL " +
           "AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<ProductEntity> searchPaginated(String query, Pageable pageable);
}

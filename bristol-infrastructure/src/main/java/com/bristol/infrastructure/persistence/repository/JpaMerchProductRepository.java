package com.bristol.infrastructure.persistence.repository;

import com.bristol.infrastructure.persistence.entity.MerchProductEntity;
import com.bristol.infrastructure.persistence.entity.MerchProductEntity.MerchCategoryEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for MerchProductEntity.
 */
@Repository
public interface JpaMerchProductRepository extends JpaRepository<MerchProductEntity, UUID> {

    @Query("SELECT m FROM MerchProductEntity m WHERE m.deletedAt IS NULL")
    List<MerchProductEntity> findAllActive();

    @Query("SELECT m FROM MerchProductEntity m WHERE m.merchTypeId = :merchTypeId AND m.deletedAt IS NULL")
    List<MerchProductEntity> findByMerchTypeId(@Param("merchTypeId") UUID merchTypeId);

    @Query("SELECT m FROM MerchProductEntity m WHERE m.merchCategory = :category AND m.deletedAt IS NULL")
    List<MerchProductEntity> findByMerchCategory(@Param("category") MerchCategoryEnum category);

    @Query("SELECT m FROM MerchProductEntity m WHERE m.isFeatured = true AND m.deletedAt IS NULL")
    List<MerchProductEntity> findFeatured();

    @Query("SELECT m FROM MerchProductEntity m WHERE m.stockQuantity > 0 AND m.deletedAt IS NULL")
    List<MerchProductEntity> findInStock();

    @Query("SELECT m FROM MerchProductEntity m WHERE m.stockQuantity <= m.lowStockThreshold AND m.stockQuantity > 0 AND m.deletedAt IS NULL")
    List<MerchProductEntity> findLowStock();

    @Query("SELECT m FROM MerchProductEntity m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%')) AND m.deletedAt IS NULL")
    List<MerchProductEntity> searchByName(@Param("name") String name);

    @Query("SELECT m FROM MerchProductEntity m WHERE LOWER(m.brand) = LOWER(:brand) AND m.deletedAt IS NULL")
    List<MerchProductEntity> findByBrand(@Param("brand") String brand);
}

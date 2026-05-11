package com.bristol.infrastructure.persistence.repository;

import com.bristol.infrastructure.persistence.entity.SpecialProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for SpecialProductEntity.
 */
@Repository
public interface JpaSpecialProductRepository extends JpaRepository<SpecialProductEntity, UUID> {

    @Query("SELECT s FROM SpecialProductEntity s WHERE s.deletedAt IS NULL")
    List<SpecialProductEntity> findAllActive();

    @Query("SELECT s FROM SpecialProductEntity s WHERE s.specialTypeId = :specialTypeId AND s.deletedAt IS NULL")
    List<SpecialProductEntity> findBySpecialTypeId(@Param("specialTypeId") UUID specialTypeId);

    @Query("SELECT s FROM SpecialProductEntity s WHERE s.requiresQuote = true AND s.deletedAt IS NULL")
    List<SpecialProductEntity> findRequiringQuote();

    @Query("SELECT s FROM SpecialProductEntity s WHERE s.requiresQuote = false AND s.deletedAt IS NULL")
    List<SpecialProductEntity> findNotRequiringQuote();

    @Query("SELECT s FROM SpecialProductEntity s WHERE s.isFeatured = true AND s.deletedAt IS NULL")
    List<SpecialProductEntity> findFeatured();

    @Query("SELECT s FROM SpecialProductEntity s WHERE s.stockQuantity > 0 AND s.deletedAt IS NULL")
    List<SpecialProductEntity> findInStock();

    @Query("SELECT s FROM SpecialProductEntity s WHERE s.stockQuantity <= s.lowStockThreshold AND s.stockQuantity > 0 AND s.deletedAt IS NULL")
    List<SpecialProductEntity> findLowStock();

    @Query("SELECT s FROM SpecialProductEntity s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%')) AND s.deletedAt IS NULL")
    List<SpecialProductEntity> searchByName(@Param("name") String name);
}

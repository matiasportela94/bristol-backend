package com.bristol.infrastructure.persistence.repository;

import com.bristol.infrastructure.persistence.entity.ProductVariantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for ProductVariantEntity.
 */
@Repository
public interface JpaProductVariantRepository extends JpaRepository<ProductVariantEntity, UUID> {

    Optional<ProductVariantEntity> findBySku(String sku);

    List<ProductVariantEntity> findByProductId(UUID productId);

    @Query("SELECT pv FROM ProductVariantEntity pv WHERE pv.productId = :productId AND pv.stockQuantity > 0 AND pv.isActive = true")
    List<ProductVariantEntity> findInStockByProductId(UUID productId);
}

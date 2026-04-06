package com.bristol.infrastructure.persistence.repository;

import com.bristol.infrastructure.persistence.entity.ProductImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for product images.
 */
@Repository
public interface JpaProductImageRepository extends JpaRepository<ProductImageEntity, UUID> {

    List<ProductImageEntity> findByProductIdOrderByDisplayOrderAscCreatedAtAsc(UUID productId);

    Optional<ProductImageEntity> findFirstByProductIdAndIsPrimaryTrue(UUID productId);

    void deleteByProductId(UUID productId);
}

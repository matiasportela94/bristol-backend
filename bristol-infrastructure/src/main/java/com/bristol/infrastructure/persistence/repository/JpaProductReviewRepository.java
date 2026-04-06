package com.bristol.infrastructure.persistence.repository;

import com.bristol.infrastructure.persistence.entity.ProductReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for ProductReviewEntity.
 */
@Repository
public interface JpaProductReviewRepository extends JpaRepository<ProductReviewEntity, UUID> {

    List<ProductReviewEntity> findByProductId(UUID productId);

    List<ProductReviewEntity> findByUserId(UUID userId);

    Optional<ProductReviewEntity> findByProductIdAndUserId(UUID productId, UUID userId);

    boolean existsByProductIdAndUserId(UUID productId, UUID userId);

    @Query("SELECT AVG(r.rating) FROM ProductReviewEntity r WHERE r.productId = :productId")
    Double getAverageRating(UUID productId);

    long countByProductId(UUID productId);
}

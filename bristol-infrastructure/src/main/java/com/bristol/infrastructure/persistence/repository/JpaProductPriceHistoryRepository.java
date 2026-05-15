package com.bristol.infrastructure.persistence.repository;

import com.bristol.infrastructure.persistence.entity.ProductPriceHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaProductPriceHistoryRepository extends JpaRepository<ProductPriceHistoryEntity, UUID> {
    List<ProductPriceHistoryEntity> findByProductIdOrderByChangedAtDesc(UUID productId);
}

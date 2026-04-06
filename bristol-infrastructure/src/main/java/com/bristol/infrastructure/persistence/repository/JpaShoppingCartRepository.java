package com.bristol.infrastructure.persistence.repository;

import com.bristol.infrastructure.persistence.entity.ShoppingCartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaShoppingCartRepository extends JpaRepository<ShoppingCartEntity, UUID> {
    Optional<ShoppingCartEntity> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
}

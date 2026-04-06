package com.bristol.infrastructure.persistence.repository;

import com.bristol.infrastructure.persistence.entity.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaCartItemRepository extends JpaRepository<CartItemEntity, UUID> {
    List<CartItemEntity> findByCartId(UUID cartId);
    List<CartItemEntity> findByCartIdIn(List<UUID> cartIds);
    void deleteByCartId(UUID cartId);
}

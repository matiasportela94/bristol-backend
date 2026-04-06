package com.bristol.infrastructure.persistence.repository;

import com.bristol.infrastructure.persistence.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for OrderItemEntity.
 */
@Repository
public interface JpaOrderItemRepository extends JpaRepository<OrderItemEntity, UUID> {

    List<OrderItemEntity> findByOrderId(UUID orderId);

    List<OrderItemEntity> findByOrderIdIn(Collection<UUID> orderIds);

    void deleteByOrderId(UUID orderId);
}

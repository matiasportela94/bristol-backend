package com.bristol.infrastructure.persistence.repository;

import com.bristol.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for OrderEntity.
 */
@Repository
public interface JpaOrderRepository extends JpaRepository<OrderEntity, UUID> {

    List<OrderEntity> findByUserId(UUID userId);

    List<OrderEntity> findByOrderStatus(OrderEntity.OrderStatusEnum status);

    List<OrderEntity> findByDistributorId(UUID distributorId);

    List<OrderEntity> findByUserIdAndOrderStatus(UUID userId, OrderEntity.OrderStatusEnum status);

    @Query("SELECT o FROM OrderEntity o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    List<OrderEntity> findByDateRange(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    @Query("SELECT o FROM OrderEntity o WHERE o.orderStatus IN ('PENDING_PAYMENT', 'PAYMENT_IN_PROCESS') " +
           "AND o.orderDate < :date")
    List<OrderEntity> findPendingOrdersOlderThan(@Param("date") Instant date);
}

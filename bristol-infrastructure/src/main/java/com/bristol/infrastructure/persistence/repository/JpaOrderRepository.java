package com.bristol.infrastructure.persistence.repository;

import com.bristol.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query(value = "SELECT nextval('order_number_seq')", nativeQuery = true)
    Long nextOrderNumber();

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

    @Query(value = "SELECT * FROM orders o WHERE " +
           "(CAST(:status AS VARCHAR) IS NULL OR o.order_status = CAST(:status AS VARCHAR)) AND " +
           "(CAST(:startDate AS TIMESTAMP) IS NULL OR o.order_date >= CAST(:startDate AS TIMESTAMP)) AND " +
           "(CAST(:endDate AS TIMESTAMP) IS NULL OR o.order_date <= CAST(:endDate AS TIMESTAMP)) AND " +
           "(CAST(:distributorId AS VARCHAR) IS NULL OR o.distributor_id = CAST(:distributorId AS UUID)) AND " +
           "(CAST(:userId AS VARCHAR) IS NULL OR o.user_id = CAST(:userId AS UUID)) AND " +
           "(CAST(:branchId AS VARCHAR) IS NULL OR o.branch_id = CAST(:branchId AS UUID)) " +
           "ORDER BY o.order_date DESC", nativeQuery = true)
    List<OrderEntity> findWithFilters(
            @Param("status") String status,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            @Param("distributorId") UUID distributorId,
            @Param("userId") UUID userId,
            @Param("branchId") UUID branchId
    );

    @Query(value = "SELECT * FROM orders o WHERE " +
           "(CAST(:orderId AS VARCHAR) IS NULL OR o.id = CAST(:orderId AS UUID)) AND " +
           "(CAST(:status AS VARCHAR) IS NULL OR o.order_status = CAST(:status AS VARCHAR)) AND " +
           "(CAST(:startDate AS TIMESTAMP) IS NULL OR o.order_date >= CAST(:startDate AS TIMESTAMP)) AND " +
           "(CAST(:endDate AS TIMESTAMP) IS NULL OR o.order_date <= CAST(:endDate AS TIMESTAMP)) AND " +
           "(CAST(:distributorId AS VARCHAR) IS NULL OR o.distributor_id = CAST(:distributorId AS UUID)) AND " +
           "(CAST(:userId AS VARCHAR) IS NULL OR o.user_id = CAST(:userId AS UUID)) AND " +
           "(CAST(:branchId AS VARCHAR) IS NULL OR o.branch_id = CAST(:branchId AS UUID)) " +
           "ORDER BY o.order_date DESC",
           countQuery = "SELECT COUNT(*) FROM orders o WHERE " +
           "(CAST(:orderId AS VARCHAR) IS NULL OR o.id = CAST(:orderId AS UUID)) AND " +
           "(CAST(:status AS VARCHAR) IS NULL OR o.order_status = CAST(:status AS VARCHAR)) AND " +
           "(CAST(:startDate AS TIMESTAMP) IS NULL OR o.order_date >= CAST(:startDate AS TIMESTAMP)) AND " +
           "(CAST(:endDate AS TIMESTAMP) IS NULL OR o.order_date <= CAST(:endDate AS TIMESTAMP)) AND " +
           "(CAST(:distributorId AS VARCHAR) IS NULL OR o.distributor_id = CAST(:distributorId AS UUID)) AND " +
           "(CAST(:userId AS VARCHAR) IS NULL OR o.user_id = CAST(:userId AS UUID)) AND " +
           "(CAST(:branchId AS VARCHAR) IS NULL OR o.branch_id = CAST(:branchId AS UUID))",
           nativeQuery = true)
    Page<OrderEntity> findWithFiltersPaginated(
            @Param("orderId") String orderId,
            @Param("status") String status,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            @Param("distributorId") String distributorId,
            @Param("userId") String userId,
            @Param("branchId") String branchId,
            Pageable pageable
    );

    @Query("SELECT COUNT(o) FROM OrderEntity o WHERE :status IS NULL OR o.orderStatus = :status")
    long countByOrderStatus(@Param("status") OrderEntity.OrderStatusEnum status);
}

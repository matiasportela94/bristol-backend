package com.bristol.domain.order;

import com.bristol.domain.distributor.DistributorId;
import com.bristol.domain.user.UserId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Order aggregate.
 */
public interface OrderRepository {

    /**
     * Save an order (create or update).
     */
    Order save(Order order);

    /**
     * Find order by ID.
     */
    Optional<Order> findById(OrderId orderId);

    /**
     * Find all orders for a user.
     */
    List<Order> findByUserId(UserId userId);

    /**
     * Find orders by status.
     */
    List<Order> findByStatus(OrderStatus status);

    /**
     * Find orders assigned to a distributor.
     */
    List<Order> findByDistributorId(DistributorId distributorId);

    /**
     * Find orders by user and status.
     */
    List<Order> findByUserIdAndStatus(UserId userId, OrderStatus status);

    /**
     * Find orders by date range.
     */
    List<Order> findByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Find pending orders older than specified date.
     */
    List<Order> findPendingOrdersOlderThan(LocalDate date);

    /**
     * Check if order exists by ID.
     */
    boolean existsById(OrderId orderId);

    /**
     * Find all orders (for admin).
     */
    List<Order> findAll();

    /**
     * Delete order (should rarely be used, prefer soft delete via cancel).
     */
    void delete(OrderId orderId);
}

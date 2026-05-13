package com.bristol.domain.order;

import com.bristol.domain.distributor.DistributorBranchId;
import com.bristol.domain.distributor.DistributorId;
import com.bristol.domain.shared.Page;
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
     * Find orders with multiple optional filters.
     * All parameters are optional (can be null).
     */
    List<Order> findWithFilters(
            OrderStatus status,
            LocalDate startDate,
            LocalDate endDate,
            DistributorId distributorId,
            UserId userId,
            DistributorBranchId branchId
    );

    /**
     * Find orders with multiple optional filters (paginated).
     * All filter parameters are optional (can be null).
     *
     * @param orderId       Order ID filter
     * @param status        Order status filter
     * @param startDate     Start date filter
     * @param endDate       End date filter
     * @param distributorId Distributor ID filter
     * @param userId        User ID filter
     * @param pageNumber    Page number (0-indexed)
     * @param pageSize      Number of items per page
     * @return Page of orders matching the filters
     */
    Page<Order> findWithFiltersPaginated(
            OrderId orderId,
            OrderStatus status,
            LocalDate startDate,
            LocalDate endDate,
            DistributorId distributorId,
            UserId userId,
            DistributorBranchId branchId,
            int pageNumber,
            int pageSize
    );

    /**
     * Count orders by status.
     * @param status Order status to count, or null to count all orders
     * @return Number of orders with the specified status
     */
    long countByStatus(OrderStatus status);

    /**
     * Delete order (should rarely be used, prefer soft delete via cancel).
     */
    void delete(OrderId orderId);
}

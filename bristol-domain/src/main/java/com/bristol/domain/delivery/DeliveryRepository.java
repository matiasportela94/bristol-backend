package com.bristol.domain.delivery;

import com.bristol.domain.order.OrderId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Delivery aggregate.
 */
public interface DeliveryRepository {

    /**
     * Save a delivery (create or update).
     */
    Delivery save(Delivery delivery);

    /**
     * Find all deliveries.
     */
    List<Delivery> findAll();

    /**
     * Find delivery by ID.
     */
    Optional<Delivery> findById(DeliveryId deliveryId);

    /**
     * Find delivery by order ID.
     */
    Optional<Delivery> findByOrderId(OrderId orderId);

    /**
     * Find deliveries by status.
     */
    List<Delivery> findByStatus(DeliveryStatus status);

    /**
     * Find deliveries scheduled for a specific date.
     */
    List<Delivery> findByScheduledDate(LocalDate date);

    /**
     * Find deliveries by delivery calendar.
     */
    List<Delivery> findByCalendar(DeliveryCalendarId calendarId);

    /**
     * Find deliveries scheduled within a date range.
     */
    List<Delivery> findByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Find overdue deliveries.
     */
    List<Delivery> findOverdue(LocalDate today);

    /**
     * Find deliveries in transit.
     */
    List<Delivery> findInTransit();

    /**
     * Find failed deliveries that can be rescheduled.
     */
    List<Delivery> findFailedDeliveries();

    /**
     * Check if order has a delivery scheduled.
     */
    boolean existsByOrderId(OrderId orderId);

    /**
     * Delete delivery (should rarely be used).
     */
    void delete(DeliveryId deliveryId);
}

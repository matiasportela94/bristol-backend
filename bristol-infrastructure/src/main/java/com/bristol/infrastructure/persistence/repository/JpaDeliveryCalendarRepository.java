package com.bristol.infrastructure.persistence.repository;

import com.bristol.infrastructure.persistence.entity.DeliveryCalendarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for DeliveryCalendarEntity.
 */
@Repository
public interface JpaDeliveryCalendarRepository extends JpaRepository<DeliveryCalendarEntity, UUID> {

    Optional<DeliveryCalendarEntity> findByDeliveryZoneIdAndDeliveryDate(UUID zoneId, LocalDate date);

    List<DeliveryCalendarEntity> findByDeliveryZoneId(UUID zoneId);

    @Query("SELECT dc FROM DeliveryCalendarEntity dc " +
           "WHERE dc.deliveryZoneId = :zoneId " +
           "AND dc.deliveryDate BETWEEN :startDate AND :endDate")
    List<DeliveryCalendarEntity> findByZoneAndDateRange(
            @Param("zoneId") UUID zoneId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT dc FROM DeliveryCalendarEntity dc " +
           "WHERE dc.deliveryZoneId = :zoneId " +
           "AND dc.deliveryDate >= :fromDate " +
           "AND dc.currentBookings < dc.capacity " +
           "ORDER BY dc.deliveryDate ASC")
    List<DeliveryCalendarEntity> findAvailableByZone(
            @Param("zoneId") UUID zoneId,
            @Param("fromDate") LocalDate fromDate
    );

    @Query("SELECT dc FROM DeliveryCalendarEntity dc " +
           "WHERE dc.deliveryDate >= :fromDate " +
           "AND dc.currentBookings >= dc.capacity")
    List<DeliveryCalendarEntity> findFullyBooked(@Param("fromDate") LocalDate fromDate);
}

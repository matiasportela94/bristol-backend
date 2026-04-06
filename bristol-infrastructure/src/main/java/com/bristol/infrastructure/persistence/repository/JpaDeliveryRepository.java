package com.bristol.infrastructure.persistence.repository;

import com.bristol.infrastructure.persistence.entity.DeliveryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for DeliveryEntity.
 */
@Repository
public interface JpaDeliveryRepository extends JpaRepository<DeliveryEntity, UUID> {

    Optional<DeliveryEntity> findByOrderId(UUID orderId);

    List<DeliveryEntity> findByDeliveryStatus(DeliveryEntity.DeliveryStatusEnum status);

    List<DeliveryEntity> findByScheduledDate(LocalDate date);

    List<DeliveryEntity> findByDeliveryCalendarId(UUID calendarId);

    @Query("SELECT d FROM DeliveryEntity d WHERE d.scheduledDate BETWEEN :startDate AND :endDate")
    List<DeliveryEntity> findByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT d FROM DeliveryEntity d WHERE d.deliveryStatus NOT IN ('DELIVERED', 'FAILED') " +
           "AND d.scheduledDate < :today")
    List<DeliveryEntity> findOverdue(@Param("today") LocalDate today);

    boolean existsByOrderId(UUID orderId);
}

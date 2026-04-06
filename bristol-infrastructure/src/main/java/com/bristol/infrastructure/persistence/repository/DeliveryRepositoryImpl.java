package com.bristol.infrastructure.persistence.repository;

import com.bristol.domain.delivery.Delivery;
import com.bristol.domain.delivery.DeliveryCalendarId;
import com.bristol.domain.delivery.DeliveryId;
import com.bristol.domain.delivery.DeliveryRepository;
import com.bristol.domain.delivery.DeliveryStatus;
import com.bristol.domain.order.OrderId;
import com.bristol.infrastructure.persistence.entity.DeliveryEntity;
import com.bristol.infrastructure.persistence.mapper.DeliveryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of DeliveryRepository port using JPA.
 */
@Component
@RequiredArgsConstructor
public class DeliveryRepositoryImpl implements DeliveryRepository {

    private final JpaDeliveryRepository jpaRepository;
    private final DeliveryMapper mapper;

    @Override
    public Delivery save(Delivery delivery) {
        var entity = mapper.toEntity(delivery);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Delivery> findById(DeliveryId deliveryId) {
        return jpaRepository.findById(deliveryId.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Delivery> findByOrderId(OrderId orderId) {
        return jpaRepository.findByOrderId(orderId.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public List<Delivery> findByStatus(DeliveryStatus status) {
        var entityStatus = DeliveryEntity.DeliveryStatusEnum.valueOf(status.name());
        return jpaRepository.findByDeliveryStatus(entityStatus).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Delivery> findByScheduledDate(LocalDate date) {
        return jpaRepository.findByScheduledDate(date).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Delivery> findByCalendar(DeliveryCalendarId calendarId) {
        return jpaRepository.findByDeliveryCalendarId(calendarId.getValue()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Delivery> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return jpaRepository.findByDateRange(startDate, endDate).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Delivery> findOverdue(LocalDate today) {
        return jpaRepository.findOverdue(today).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Delivery> findInTransit() {
        return findByStatus(DeliveryStatus.IN_TRANSIT);
    }

    @Override
    public List<Delivery> findFailedDeliveries() {
        return findByStatus(DeliveryStatus.FAILED);
    }

    @Override
    public boolean existsByOrderId(OrderId orderId) {
        return jpaRepository.existsByOrderId(orderId.getValue());
    }

    @Override
    public void delete(DeliveryId deliveryId) {
        jpaRepository.deleteById(deliveryId.getValue());
    }
}

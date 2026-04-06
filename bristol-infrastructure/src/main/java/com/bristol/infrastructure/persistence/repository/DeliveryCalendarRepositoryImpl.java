package com.bristol.infrastructure.persistence.repository;

import com.bristol.domain.delivery.DeliveryCalendar;
import com.bristol.domain.delivery.DeliveryCalendarId;
import com.bristol.domain.delivery.DeliveryCalendarRepository;
import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.infrastructure.persistence.mapper.DeliveryCalendarMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of DeliveryCalendarRepository port using JPA.
 */
@Component
@RequiredArgsConstructor
public class DeliveryCalendarRepositoryImpl implements DeliveryCalendarRepository {

    private final JpaDeliveryCalendarRepository jpaRepository;
    private final DeliveryCalendarMapper mapper;

    @Override
    public DeliveryCalendar save(DeliveryCalendar deliveryCalendar) {
        var entity = mapper.toEntity(deliveryCalendar);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<DeliveryCalendar> findById(DeliveryCalendarId calendarId) {
        return jpaRepository.findById(calendarId.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<DeliveryCalendar> findByZoneAndDate(DeliveryZoneId zoneId, LocalDate date) {
        return jpaRepository.findByDeliveryZoneIdAndDeliveryDate(zoneId.getValue(), date)
                .map(mapper::toDomain);
    }

    @Override
    public List<DeliveryCalendar> findByZone(DeliveryZoneId zoneId) {
        return jpaRepository.findByDeliveryZoneId(zoneId.getValue()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeliveryCalendar> findByZoneAndDateRange(DeliveryZoneId zoneId, LocalDate startDate, LocalDate endDate) {
        return jpaRepository.findByZoneAndDateRange(zoneId.getValue(), startDate, endDate).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeliveryCalendar> findAvailableByZone(DeliveryZoneId zoneId, LocalDate fromDate) {
        return jpaRepository.findAvailableByZone(zoneId.getValue(), fromDate).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeliveryCalendar> findFullyBooked(LocalDate fromDate) {
        return jpaRepository.findFullyBooked(fromDate).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasCapacity(DeliveryZoneId zoneId, LocalDate date) {
        return jpaRepository.findByDeliveryZoneIdAndDeliveryDate(zoneId.getValue(), date)
                .map(cal -> cal.getCurrentBookings() < cal.getCapacity())
                .orElse(false);
    }

    @Override
    public void delete(DeliveryCalendarId calendarId) {
        jpaRepository.deleteById(calendarId.getValue());
    }
}

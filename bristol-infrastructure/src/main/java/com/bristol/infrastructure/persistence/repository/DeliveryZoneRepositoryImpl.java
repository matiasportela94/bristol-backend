package com.bristol.infrastructure.persistence.repository;

import com.bristol.domain.delivery.DeliveryZone;
import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.delivery.DeliveryZoneRepository;
import com.bristol.infrastructure.persistence.mapper.DeliveryZoneMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of DeliveryZoneRepository port using JPA.
 */
@Component
@RequiredArgsConstructor
public class DeliveryZoneRepositoryImpl implements DeliveryZoneRepository {

    private final JpaDeliveryZoneRepository jpaRepository;
    private final DeliveryZoneMapper mapper;

    @Override
    public DeliveryZone save(DeliveryZone zone) {
        var entity = mapper.toEntity(zone);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<DeliveryZone> findById(DeliveryZoneId id) {
        return jpaRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public List<DeliveryZone> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<DeliveryZone> findByName(String name) {
        return jpaRepository.findByName(name)
                .map(mapper::toDomain);
    }

    @Override
    public List<DeliveryZone> findAllActive() {
        return jpaRepository.findByIsActive(true).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }

    @Override
    public void delete(DeliveryZoneId id) {
        jpaRepository.deleteById(id.getValue());
    }
}

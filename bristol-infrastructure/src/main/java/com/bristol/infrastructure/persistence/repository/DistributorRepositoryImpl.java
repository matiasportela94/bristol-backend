package com.bristol.infrastructure.persistence.repository;

import com.bristol.domain.distributor.Distributor;
import com.bristol.domain.distributor.DistributorId;
import com.bristol.domain.distributor.DistributorRepository;
import com.bristol.domain.distributor.DistributorStatus;
import com.bristol.domain.user.UserId;
import com.bristol.infrastructure.persistence.entity.DistributorEntity;
import com.bristol.infrastructure.persistence.mapper.DistributorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of DistributorRepository port using JPA.
 */
@Component
@RequiredArgsConstructor
public class DistributorRepositoryImpl implements DistributorRepository {

    private final JpaDistributorRepository jpaRepository;
    private final DistributorMapper mapper;
    private final DistributorStatsCalculator statsCalculator;

    @Override
    public Distributor save(Distributor distributor) {
        var entity = mapper.toEntity(distributor);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Distributor> findById(DistributorId id) {
        return jpaRepository.findById(id.getValue())
                .map(this::enrichWithCalculatedStats);
    }

    @Override
    public List<Distributor> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::enrichWithCalculatedStats)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Distributor> findByUserId(UserId userId) {
        return jpaRepository.findByUserId(userId.getValue())
                .map(this::enrichWithCalculatedStats);
    }

    @Override
    public List<Distributor> findByStatus(DistributorStatus status) {
        var entityStatus = DistributorEntity.DistributorStatusEnum.valueOf(status.name());
        return jpaRepository.findByStatus(entityStatus).stream()
                .map(this::enrichWithCalculatedStats)
                .collect(Collectors.toList());
    }

    @Override
    public List<Distributor> findByDeliveryZone(com.bristol.domain.delivery.DeliveryZoneId zoneId) {
        // TODO: This requires adding delivery_zone_id to DistributorEntity
        // For now, return empty list as this field is not in the current schema
        return List.of();
    }

    @Override
    public boolean existsByCuit(String cuit) {
        return jpaRepository.existsByCuit(cuit);
    }

    @Override
    public boolean existsByUserId(UserId userId) {
        return jpaRepository.existsByUserId(userId.getValue());
    }

    @Override
    public Optional<Distributor> findByCuit(String cuit) {
        return jpaRepository.findByCuit(cuit)
                .map(this::enrichWithCalculatedStats);
    }

    @Override
    public void delete(DistributorId id) {
        jpaRepository.deleteById(id.getValue());
    }

    /**
     * Enrich distributor with calculated statistics from orders.
     * Calculates totalOrders on-the-fly while keeping totalSpent and totalBeers from DB.
     */
    private Distributor enrichWithCalculatedStats(DistributorEntity entity) {
        Distributor distributor = mapper.toDomain(entity);

        // Calculate totalOrders from orders table
        int totalOrders = statsCalculator.calculateTotalOrders(distributor.getUserId());

        // Rebuild with calculated totalOrders
        return distributor.toBuilder()
                .totalOrders(totalOrders)
                .build();
    }
}

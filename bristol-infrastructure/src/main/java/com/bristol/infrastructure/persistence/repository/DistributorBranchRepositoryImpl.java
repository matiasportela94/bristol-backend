package com.bristol.infrastructure.persistence.repository;

import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.distributor.DistributorBranch;
import com.bristol.domain.distributor.DistributorBranchId;
import com.bristol.domain.distributor.DistributorBranchRepository;
import com.bristol.domain.distributor.DistributorId;
import com.bristol.infrastructure.persistence.entity.DistributorBranchEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DistributorBranchRepositoryImpl implements DistributorBranchRepository {

    private final JpaDistributorBranchRepository jpa;

    @Override
    public DistributorBranch save(DistributorBranch branch) {
        return toDomain(jpa.save(toEntity(branch)));
    }

    @Override
    public Optional<DistributorBranch> findById(DistributorBranchId id) {
        return jpa.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public List<DistributorBranch> findByDistributorId(DistributorId distributorId) {
        return jpa.findByDistributorId(distributorId.getValue())
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<DistributorBranch> findAll() {
        return jpa.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void delete(DistributorBranchId id) {
        jpa.deleteById(id.getValue());
    }

    @Override
    public boolean existsById(DistributorBranchId id) {
        return jpa.existsById(id.getValue());
    }

    private DistributorBranch toDomain(DistributorBranchEntity e) {
        return DistributorBranch.builder()
                .id(new DistributorBranchId(e.getId()))
                .distributorId(new DistributorId(e.getDistributorId()))
                .name(e.getName())
                .address(e.getAddress())
                .city(e.getCity())
                .province(e.getProvince())
                .codigoPostal(e.getCodigoPostal())
                .deliveryZoneId(e.getDeliveryZoneId() != null ? new DeliveryZoneId(e.getDeliveryZoneId()) : null)
                .active(Boolean.TRUE.equals(e.getActive()))
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    private DistributorBranchEntity toEntity(DistributorBranch b) {
        return DistributorBranchEntity.builder()
                .id(b.getId().getValue())
                .distributorId(b.getDistributorId().getValue())
                .name(b.getName())
                .address(b.getAddress())
                .city(b.getCity())
                .province(b.getProvince())
                .codigoPostal(b.getCodigoPostal())
                .deliveryZoneId(b.getDeliveryZoneId() != null ? b.getDeliveryZoneId().getValue() : null)
                .active(b.isActive())
                .createdAt(b.getCreatedAt())
                .updatedAt(b.getUpdatedAt())
                .build();
    }
}

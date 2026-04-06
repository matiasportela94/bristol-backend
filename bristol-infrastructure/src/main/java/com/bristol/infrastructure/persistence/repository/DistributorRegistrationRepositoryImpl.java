package com.bristol.infrastructure.persistence.repository;

import com.bristol.domain.distributor.DistributorRegistrationRepository;
import com.bristol.domain.distributor.DistributorRegistrationRequest;
import com.bristol.domain.distributor.DistributorRegistrationRequestId;
import com.bristol.domain.distributor.RegistrationStatus;
import com.bristol.infrastructure.persistence.entity.DistributorRegistrationRequestEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of DistributorRegistrationRepository using JPA.
 */
@Component
@RequiredArgsConstructor
public class DistributorRegistrationRepositoryImpl implements DistributorRegistrationRepository {

    private final JpaDistributorRegistrationRequestRepository jpaRepository;

    @Override
    public DistributorRegistrationRequest save(DistributorRegistrationRequest request) {
        DistributorRegistrationRequestEntity entity = toEntity(request);
        DistributorRegistrationRequestEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<DistributorRegistrationRequest> findById(DistributorRegistrationRequestId id) {
        return jpaRepository.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public List<DistributorRegistrationRequest> findByStatus(RegistrationStatus status) {
        return jpaRepository.findByStatus(status).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<DistributorRegistrationRequest> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public void delete(DistributorRegistrationRequestId id) {
        jpaRepository.deleteById(id.getValue());
    }

    private DistributorRegistrationRequest toDomain(DistributorRegistrationRequestEntity entity) {
        return DistributorRegistrationRequest.builder()
                .id(new DistributorRegistrationRequestId(entity.getId()))
                .razonSocial(entity.getRazonSocial())
                .cuit(entity.getCuit())
                .email(entity.getEmail())
                .telefono(entity.getTelefono())
                .provincia(entity.getProvincia())
                .ciudad(entity.getCiudad())
                .direccion(entity.getDireccion())
                .codigoPostal(entity.getCodigoPostal())
                .status(entity.getStatus())
                .rejectionReason(entity.getRejectionReason())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private DistributorRegistrationRequestEntity toEntity(DistributorRegistrationRequest domain) {
        return DistributorRegistrationRequestEntity.builder()
                .id(domain.getId().getValue())
                .razonSocial(domain.getRazonSocial())
                .cuit(domain.getCuit())
                .email(domain.getEmail())
                .telefono(domain.getTelefono())
                .provincia(domain.getProvincia())
                .ciudad(domain.getCiudad())
                .direccion(domain.getDireccion())
                .codigoPostal(domain.getCodigoPostal())
                .status(domain.getStatus())
                .rejectionReason(domain.getRejectionReason())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}

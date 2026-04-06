package com.bristol.infrastructure.persistence.repository;

import com.bristol.infrastructure.persistence.entity.DistributorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for DistributorEntity.
 */
@Repository
public interface JpaDistributorRepository extends JpaRepository<DistributorEntity, UUID> {

    Optional<DistributorEntity> findByUserId(UUID userId);

    List<DistributorEntity> findByStatus(DistributorEntity.DistributorStatusEnum status);

    boolean existsByCuit(String cuit);

    boolean existsByUserId(UUID userId);

    Optional<DistributorEntity> findByCuit(String cuit);
}

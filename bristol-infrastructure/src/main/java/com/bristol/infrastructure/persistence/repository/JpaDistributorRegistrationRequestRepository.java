package com.bristol.infrastructure.persistence.repository;

import com.bristol.domain.distributor.RegistrationStatus;
import com.bristol.infrastructure.persistence.entity.DistributorRegistrationRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for DistributorRegistrationRequestEntity.
 */
@Repository
public interface JpaDistributorRegistrationRequestRepository extends JpaRepository<DistributorRegistrationRequestEntity, UUID> {

    List<DistributorRegistrationRequestEntity> findByStatus(RegistrationStatus status);

    Optional<DistributorRegistrationRequestEntity> findByEmail(String email);
}

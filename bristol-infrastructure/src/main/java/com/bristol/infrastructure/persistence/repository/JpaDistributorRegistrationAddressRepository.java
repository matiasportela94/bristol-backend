package com.bristol.infrastructure.persistence.repository;

import com.bristol.infrastructure.persistence.entity.DistributorRegistrationAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaDistributorRegistrationAddressRepository extends JpaRepository<DistributorRegistrationAddressEntity, UUID> {

    List<DistributorRegistrationAddressEntity> findByRegistrationRequestIdOrderByCreatedAtAsc(UUID registrationRequestId);
}

package com.bristol.infrastructure.persistence.repository;

import com.bristol.infrastructure.persistence.entity.RegistrationDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for registration documents.
 */
@Repository
public interface JpaRegistrationDocumentRepository extends JpaRepository<RegistrationDocumentEntity, UUID> {

    List<RegistrationDocumentEntity> findByRegistrationRequestIdOrderByCreatedAtAsc(UUID registrationRequestId);

    List<RegistrationDocumentEntity> findByDistributorIdOrderByCreatedAtAsc(UUID distributorId);
}

package com.bristol.domain.distributor;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for DistributorRegistrationRequest aggregate.
 */
public interface DistributorRegistrationRepository {

    DistributorRegistrationRequest save(DistributorRegistrationRequest request);

    Optional<DistributorRegistrationRequest> findById(DistributorRegistrationRequestId id);

    List<DistributorRegistrationRequest> findByStatus(RegistrationStatus status);

    Optional<DistributorRegistrationRequest> findByEmail(String email);

    void delete(DistributorRegistrationRequestId id);
}

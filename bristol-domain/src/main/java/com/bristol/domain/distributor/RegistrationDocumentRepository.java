package com.bristol.domain.distributor;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for registration documents.
 */
public interface RegistrationDocumentRepository {

    RegistrationDocument save(RegistrationDocument document);

    Optional<RegistrationDocument> findById(RegistrationDocumentId id);

    List<RegistrationDocument> findByRegistrationRequestId(DistributorRegistrationRequestId registrationRequestId);

    List<RegistrationDocument> findByDistributorId(DistributorId distributorId);

    void delete(RegistrationDocumentId id);
}

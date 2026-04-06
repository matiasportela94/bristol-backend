package com.bristol.application.distributor.usecase;

import com.bristol.application.distributor.dto.UploadedFileDto;
import com.bristol.domain.distributor.Distributor;
import com.bristol.domain.distributor.DistributorId;
import com.bristol.domain.distributor.DistributorRepository;
import com.bristol.domain.distributor.RegistrationDocument;
import com.bristol.domain.distributor.RegistrationDocumentId;
import com.bristol.domain.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Resolves supporting documents for approved distributors from their registration request.
 */
@Component
@RequiredArgsConstructor
public class DistributorDocumentQueryService {

    private final DistributorRepository distributorRepository;
    private final RegistrationDocumentService registrationDocumentService;

    public List<UploadedFileDto> getDocuments(Distributor distributor) {
        return registrationDocumentService.toDtos(distributor.getId());
    }

    public RegistrationDocument getDocumentForDistributor(Distributor distributor, RegistrationDocumentId documentId) {
        return registrationDocumentService.getDocumentForDistributor(distributor.getId(), documentId);
    }

    public RegistrationDocument getDocumentForDistributor(String distributorId, String documentId) {
        Distributor distributor = distributorRepository.findById(new DistributorId(distributorId))
                .orElseThrow(() -> new ValidationException("Distributor not found: " + distributorId));

        return getDocumentForDistributor(distributor, new RegistrationDocumentId(java.util.UUID.fromString(documentId)));
    }
}

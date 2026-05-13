package com.bristol.application.distributor.usecase;

import com.bristol.application.distributor.dto.DistributorRegistrationDto;
import com.bristol.application.distributor.mapper.DistributorRegistrationMapper;
import com.bristol.domain.distributor.DistributorRegistrationRepository;
import com.bristol.domain.distributor.DistributorRegistrationRequestId;
import com.bristol.domain.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetDistributorRegistrationByIdUseCase {

    private final DistributorRegistrationRepository registrationRepository;
    private final DistributorRegistrationMapper mapper;
    private final RegistrationDocumentService registrationDocumentService;

    @Transactional(readOnly = true)
    public DistributorRegistrationDto execute(String registrationId) {
        DistributorRegistrationRequestId id = new DistributorRegistrationRequestId(UUID.fromString(registrationId));

        return registrationRepository.findById(id)
                .map(registration -> mapper.toDto(
                        registration,
                        registrationDocumentService.toDtos(registration.getId())
                ))
                .orElseThrow(() -> new ValidationException("Registration request not found: " + registrationId));
    }
}

package com.bristol.application.distributor.usecase;

import com.bristol.application.distributor.dto.DistributorRegistrationDto;
import com.bristol.application.distributor.mapper.DistributorRegistrationMapper;
import com.bristol.domain.distributor.DistributorRegistrationRepository;
import com.bristol.domain.distributor.RegistrationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use case to get all pending distributor registration requests.
 */
@Service
@RequiredArgsConstructor
public class GetPendingRegistrationsUseCase {

    private final DistributorRegistrationRepository registrationRepository;
    private final DistributorRegistrationMapper mapper;
    private final RegistrationDocumentService registrationDocumentService;

    @Transactional(readOnly = true)
    public List<DistributorRegistrationDto> execute() {
        return registrationRepository.findByStatus(RegistrationStatus.PENDING)
                .stream()
                .map(registration -> mapper.toDto(
                        registration,
                        registrationDocumentService.toDtos(registration.getId())
                ))
                .collect(Collectors.toList());
    }
}

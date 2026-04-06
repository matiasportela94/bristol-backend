package com.bristol.application.distributor.usecase;

import com.bristol.application.distributor.dto.DistributorRegistrationDto;
import com.bristol.application.distributor.dto.RejectDistributorRegistrationRequest;
import com.bristol.application.distributor.mapper.DistributorRegistrationMapper;
import com.bristol.domain.distributor.DistributorRegistrationRepository;
import com.bristol.domain.distributor.DistributorRegistrationRequest;
import com.bristol.domain.distributor.DistributorRegistrationRequestId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Use case to reject a distributor registration request.
 */
@Service
@RequiredArgsConstructor
public class RejectDistributorRegistrationUseCase {

    private final DistributorRegistrationRepository registrationRepository;
    private final DistributorRegistrationMapper mapper;
    private final DistributorRegistrationNotificationService notificationService;

    @Transactional
    public DistributorRegistrationDto execute(String registrationId, RejectDistributorRegistrationRequest request) {
        // Find registration request
        DistributorRegistrationRequestId id = new DistributorRegistrationRequestId(UUID.fromString(registrationId));
        DistributorRegistrationRequest registration = registrationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Registration request not found: " + registrationId));

        // Reject the registration (changes status to REJECTED and sets reason)
        registration = registration.reject(request.getReason(), Instant.now());
        registrationRepository.delete(registration.getId());

        return mapper.toDto(
                registration,
                List.of()
        );
    }
}

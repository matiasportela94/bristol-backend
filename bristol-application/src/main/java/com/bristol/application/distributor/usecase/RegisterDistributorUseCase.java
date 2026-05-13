package com.bristol.application.distributor.usecase;

import com.bristol.application.distributor.dto.CreateDistributorRegistrationRequest;
import com.bristol.application.distributor.dto.DistributorRegistrationDto;
import com.bristol.application.distributor.mapper.DistributorRegistrationMapper;
import com.bristol.domain.distributor.DistributorRegistrationRepository;
import com.bristol.domain.distributor.DistributorRegistrationRequest;
import com.bristol.domain.user.UserRepository;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Use case to register a new distributor request.
 */
@Service
@RequiredArgsConstructor
public class RegisterDistributorUseCase {

    private final DistributorRegistrationRepository registrationRepository;
    private final DistributorRegistrationMapper mapper;
    private final RegistrationDocumentService registrationDocumentService;
    private final UserRepository userRepository;
    private final TimeProvider timeProvider;

    @Transactional
    public DistributorRegistrationDto execute(CreateDistributorRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("A user with this email is already registered");
        }

        // Check if email already exists
        registrationRepository.findByEmail(request.getEmail())
                .ifPresent(existing -> {
                    throw new ValidationException("A registration request with this email already exists");
                });

        // Create registration request in PENDING status
        DistributorRegistrationRequest registration = DistributorRegistrationRequest.create(
                request.getRazonSocial(),
                request.getCuit(),
                request.getEmail(),
                request.getTelefono(),
                request.getProvincia(),
                request.getCiudad(),
                request.getDireccion(),
                request.getCodigoPostal(),
                timeProvider.now()
        );

        // Save to repository
        DistributorRegistrationRequest savedRegistration = registrationRepository.save(registration);
        registrationDocumentService.createDocuments(savedRegistration.getId(), request.getDocuments());

        return mapper.toDto(
                savedRegistration,
                List.of(),
                registrationDocumentService.toDtos(savedRegistration.getId())
        );
    }
}

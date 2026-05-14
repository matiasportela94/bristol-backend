package com.bristol.application.distributor.usecase;

import com.bristol.application.distributor.dto.DistributorRegistrationDto;
import com.bristol.application.distributor.mapper.DistributorRegistrationMapper;
import com.bristol.domain.distributor.Distributor;
import com.bristol.domain.distributor.DistributorRegistrationRepository;
import com.bristol.domain.distributor.DistributorRegistrationRequest;
import com.bristol.domain.distributor.DistributorRegistrationRequestId;
import com.bristol.domain.distributor.DistributorRepository;
import com.bristol.domain.distributor.DistributorStatus;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.user.User;
import com.bristol.domain.user.UserRepository;
import com.bristol.domain.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Use case to approve a distributor registration request.
 * Creates both User and Distributor entities upon approval.
 */
@Service
@RequiredArgsConstructor
public class ApproveDistributorRegistrationUseCase {
    private static final String DEFAULT_DISTRIBUTOR_PASSWORD = "contraseña123";

    private final DistributorRegistrationRepository registrationRepository;
    private final UserRepository userRepository;
    private final DistributorRepository distributorRepository;
    private final PasswordEncoder passwordEncoder;
    private final DistributorRegistrationMapper mapper;
    private final RegistrationDocumentService registrationDocumentService;
    private final DistributorRegistrationNotificationService notificationService;
    private final TimeProvider timeProvider;

    @Transactional
    public DistributorRegistrationDto execute(String registrationId) {
        Instant now = timeProvider.now();

        // Find registration request
        DistributorRegistrationRequestId id = new DistributorRegistrationRequestId(UUID.fromString(registrationId));
        DistributorRegistrationRequest registration = registrationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Registration request not found: " + registrationId));

        // Approve the registration (changes status to APPROVED)
        registration = registration.approve(now);

        String temporaryPassword = DEFAULT_DISTRIBUTOR_PASSWORD;
        User savedUser = saveApprovedUser(registration, temporaryPassword, now);
        Distributor savedDistributor = distributorRepository.save(upsertApprovedDistributor(registration, savedUser, now));

        // Link the user to their distributor now that the distributor ID is known
        userRepository.save(savedUser.toBuilder()
                .distributorId(savedDistributor.getId())
                .build());

        registrationDocumentService.assignDocumentsToDistributor(registration.getId(), savedDistributor.getId());
        registrationRepository.delete(registration.getId());

        return mapper.toDto(registration, List.of(), List.of());
    }

    private User saveApprovedUser(
            DistributorRegistrationRequest registration,
            String temporaryPassword,
            Instant now
    ) {
        String hashedPassword = passwordEncoder.encode(temporaryPassword);

        return userRepository.findByEmail(registration.getEmail())
                .map(existingUser -> existingUser.toBuilder()
                        .firstName(extractFirstName(registration.getRazonSocial()))
                        .lastName(extractLastName(registration.getRazonSocial()))
                        .phone(registration.getTelefono())
                        .passwordHash(hashedPassword)
                        .role(UserRole.DISTRIBUTOR)
                        .isDistributor(true)
                        .updatedAt(now)
                        .build())
                .map(userRepository::save)
                .orElseGet(() -> {
                    User newUser = User.create(
                                    registration.getEmail(),
                                    hashedPassword,
                                    extractFirstName(registration.getRazonSocial()),
                                    extractLastName(registration.getRazonSocial()),
                                    UserRole.DISTRIBUTOR,
                                    now
                            ).toBuilder()
                            .phone(registration.getTelefono())
                            .isDistributor(true)
                            .build();

                    return userRepository.save(newUser);
                });
    }

    private Distributor upsertApprovedDistributor(
            DistributorRegistrationRequest registration,
            User savedUser,
            Instant now
    ) {
        return distributorRepository.findByCuit(registration.getCuit())
                .or(() -> distributorRepository.findByUserId(savedUser.getId()))
                .map(existingDistributor -> existingDistributor.toBuilder()
                        .userId(savedUser.getId())
                        .address(buildFiscalAddress(registration))
                        .phone(registration.getTelefono())
                        .cuit(registration.getCuit())
                        .razonSocial(registration.getRazonSocial())
                        .status(DistributorStatus.APPROVED)
                        .updatedAt(now)
                        .build())
                .orElseGet(() -> Distributor.create(
                                savedUser.getId(),
                                buildFiscalAddress(registration),
                                registration.getTelefono(),
                                "",
                                registration.getCuit(),
                                registration.getRazonSocial(),
                                null,
                                now
                        ).approve(now));
    }

    private String buildFiscalAddress(DistributorRegistrationRequest registration) {
        StringBuilder fullAddress = new StringBuilder(registration.getDireccion());

        if (registration.getCiudad() != null && !registration.getCiudad().isBlank()) {
            fullAddress.append(", ").append(registration.getCiudad());
        }
        if (registration.getProvincia() != null && !registration.getProvincia().isBlank()) {
            fullAddress.append(", ").append(registration.getProvincia());
        }
        if (registration.getCodigoPostal() != null && !registration.getCodigoPostal().isBlank()) {
            fullAddress.append(" ").append(registration.getCodigoPostal());
        }

        return fullAddress.toString();
    }

    /**
     * Extract first name from razon social.
     * In production, this should be a separate field in the form.
     */
    private String extractFirstName(String razonSocial) {
        String[] parts = razonSocial.split(" ");
        return parts.length > 0 ? parts[0] : razonSocial;
    }

    /**
     * Extract last name from razon social.
     * In production, this should be a separate field in the form.
     */
    private String extractLastName(String razonSocial) {
        String[] parts = razonSocial.split(" ");
        return parts.length > 1 ? parts[parts.length - 1] : "";
    }
}

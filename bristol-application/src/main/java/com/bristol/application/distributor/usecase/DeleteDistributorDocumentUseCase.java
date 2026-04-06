package com.bristol.application.distributor.usecase;

import com.bristol.application.distributor.dto.DistributorDto;
import com.bristol.domain.distributor.Distributor;
import com.bristol.domain.distributor.RegistrationDocumentId;
import com.bristol.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteDistributorDocumentUseCase {

    private final RegistrationDocumentService registrationDocumentService;
    private final DistributorMapper distributorMapper;
    private final UserRepository userRepository;

    @Transactional
    public DistributorDto execute(Distributor distributor, String documentId) {
        registrationDocumentService.deleteDocumentForDistributor(
                distributor.getId(),
                new RegistrationDocumentId(UUID.fromString(documentId))
        );

        String userEmail = userRepository.findById(distributor.getUserId())
                .map(user -> user.getEmail())
                .orElse(null);

        return distributorMapper.toDto(
                distributor,
                userEmail,
                registrationDocumentService.toDtos(distributor.getId())
        );
    }
}

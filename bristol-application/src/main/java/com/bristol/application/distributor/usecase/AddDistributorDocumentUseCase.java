package com.bristol.application.distributor.usecase;

import com.bristol.application.distributor.dto.DistributorDto;
import com.bristol.domain.distributor.Distributor;
import com.bristol.domain.user.User;
import com.bristol.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddDistributorDocumentUseCase {

    private final RegistrationDocumentService registrationDocumentService;
    private final DistributorMapper distributorMapper;
    private final UserRepository userRepository;

    @Transactional
    public DistributorDto execute(
            Distributor distributor,
            String fileName,
            String contentType,
            String documentType,
            long fileSize,
            byte[] fileData
    ) {
        registrationDocumentService.createDocumentForDistributor(
                distributor.getId(),
                fileName,
                contentType,
                documentType,
                fileSize,
                fileData
        );

        String userEmail = userRepository.findById(distributor.getUserId())
                .map(User::getEmail)
                .orElse(null);

        return distributorMapper.toDto(
                distributor,
                userEmail,
                registrationDocumentService.toDtos(distributor.getId())
        );
    }
}

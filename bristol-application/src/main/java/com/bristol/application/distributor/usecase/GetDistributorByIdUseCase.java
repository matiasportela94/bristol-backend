package com.bristol.application.distributor.usecase;

import com.bristol.application.distributor.dto.DistributorDto;
import com.bristol.domain.distributor.DistributorId;
import com.bristol.domain.distributor.DistributorRepository;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetDistributorByIdUseCase {
    private final DistributorRepository distributorRepository;
    private final UserRepository userRepository;
    private final DistributorMapper distributorMapper;
    private final DistributorDocumentQueryService distributorDocumentQueryService;

    @Transactional(readOnly = true)
    public DistributorDto execute(String distributorId) {
        DistributorId id = new DistributorId(distributorId);
        return distributorRepository.findById(id)
                .map(distributor -> {
                    String userEmail = userRepository.findById(distributor.getUserId())
                            .map(user -> user.getEmail())
                            .orElse(null);
                    return distributorMapper.toDto(
                            distributor,
                            userEmail,
                            distributorDocumentQueryService.getDocuments(distributor)
                    );
                })
                .orElseThrow(() -> new ValidationException("Distributor not found: " + distributorId));
    }
}

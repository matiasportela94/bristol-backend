package com.bristol.application.distributor.usecase;

import com.bristol.application.distributor.dto.DistributorDto;
import com.bristol.domain.distributor.Distributor;
import com.bristol.domain.distributor.DistributorRepository;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.user.UserId;
import com.bristol.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetDistributorByUserIdUseCase {

    private final DistributorRepository distributorRepository;
    private final UserRepository userRepository;
    private final DistributorMapper distributorMapper;
    private final DistributorDocumentQueryService distributorDocumentQueryService;

    @Transactional(readOnly = true)
    public DistributorDto execute(String userId) {
        Distributor distributor = distributorRepository.findByUserId(new UserId(userId))
                .orElseThrow(() -> new ValidationException("Distributor not found for user: " + userId));

        String userEmail = userRepository.findById(distributor.getUserId())
                .map(user -> user.getEmail())
                .orElse(null);

        return distributorMapper.toDto(
                distributor,
                userEmail,
                distributorDocumentQueryService.getDocuments(distributor)
        );
    }
}

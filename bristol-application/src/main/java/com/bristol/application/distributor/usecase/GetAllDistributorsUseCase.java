package com.bristol.application.distributor.usecase;

import com.bristol.application.distributor.dto.DistributorDto;
import com.bristol.domain.distributor.DistributorRepository;
import com.bristol.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAllDistributorsUseCase {
    private final DistributorRepository distributorRepository;
    private final UserRepository userRepository;
    private final DistributorMapper distributorMapper;
    private final DistributorDocumentQueryService distributorDocumentQueryService;

    @Transactional(readOnly = true)
    public List<DistributorDto> execute() {
        return distributorRepository.findAll().stream()
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
                .collect(Collectors.toList());
    }
}

package com.bristol.application.distributorbranch.usecase;

import com.bristol.application.distributorbranch.dto.DistributorBranchDto;
import com.bristol.domain.distributor.DistributorBranchRepository;
import com.bristol.domain.distributor.DistributorId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetBranchesByDistributorUseCase {

    private final DistributorBranchRepository branchRepository;
    private final DistributorBranchMapper mapper;

    @Transactional(readOnly = true)
    public List<DistributorBranchDto> execute(String distributorId) {
        return branchRepository.findByDistributorId(new DistributorId(distributorId))
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }
}

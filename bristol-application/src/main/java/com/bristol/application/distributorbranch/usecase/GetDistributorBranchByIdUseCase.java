package com.bristol.application.distributorbranch.usecase;

import com.bristol.application.distributorbranch.dto.DistributorBranchDto;
import com.bristol.domain.distributor.DistributorBranchId;
import com.bristol.domain.distributor.DistributorBranchRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetDistributorBranchByIdUseCase {

    private final DistributorBranchRepository branchRepository;
    private final DistributorBranchMapper mapper;

    @Transactional(readOnly = true)
    public DistributorBranchDto execute(String branchId) {
        return branchRepository.findById(new DistributorBranchId(branchId))
                .map(mapper::toDto)
                .orElseThrow(() -> new NotFoundException("Branch", branchId));
    }
}

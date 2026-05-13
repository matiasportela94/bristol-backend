package com.bristol.application.distributorbranch.usecase;

import com.bristol.domain.distributor.DistributorBranchId;
import com.bristol.domain.distributor.DistributorBranchRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteDistributorBranchUseCase {

    private final DistributorBranchRepository branchRepository;

    @Transactional
    public void execute(String branchId) {
        DistributorBranchId id = new DistributorBranchId(branchId);
        if (!branchRepository.existsById(id)) {
            throw new NotFoundException("Branch", branchId);
        }
        branchRepository.delete(id);
    }
}

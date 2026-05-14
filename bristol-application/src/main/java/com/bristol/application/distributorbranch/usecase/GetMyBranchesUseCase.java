package com.bristol.application.distributorbranch.usecase;

import com.bristol.application.distributorbranch.dto.DistributorBranchDto;
import com.bristol.domain.distributor.DistributorBranchId;
import com.bristol.domain.distributor.DistributorBranchRepository;
import com.bristol.domain.distributor.DistributorId;
import com.bristol.domain.shared.exception.NotFoundException;
import com.bristol.domain.user.User;
import com.bristol.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Returns the branches visible to the authenticated user.
 *
 * DISTRIBUTOR        → all branches of their distributor
 * DISTRIBUTOR_BRANCH → only their assigned branch
 * ADMIN              → must use the scoped endpoint (returns empty list here)
 */
@Service
@RequiredArgsConstructor
public class GetMyBranchesUseCase {

    private final UserRepository userRepository;
    private final DistributorBranchRepository branchRepository;
    private final DistributorBranchMapper mapper;

    @Transactional(readOnly = true)
    public List<DistributorBranchDto> execute(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User", email));

        // DISTRIBUTOR_BRANCH: return only the user's own branch
        DistributorBranchId branchId = user.getBranchId();
        if (branchId != null) {
            return branchRepository.findById(branchId)
                    .map(mapper::toDto)
                    .map(List::of)
                    .orElse(List.of());
        }

        // DISTRIBUTOR / ADMIN with distributorId: return all branches
        DistributorId distributorId = user.getDistributorId();
        if (distributorId != null) {
            return branchRepository.findByDistributorId(distributorId)
                    .stream().map(mapper::toDto).toList();
        }

        return List.of();
    }
}

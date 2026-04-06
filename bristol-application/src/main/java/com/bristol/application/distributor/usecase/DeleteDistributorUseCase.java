package com.bristol.application.distributor.usecase;

import com.bristol.domain.distributor.DistributorId;
import com.bristol.domain.distributor.DistributorRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use case to delete a distributor.
 */
@Service
@RequiredArgsConstructor
public class DeleteDistributorUseCase {

    private final DistributorRepository distributorRepository;

    @Transactional
    public void execute(String id) {
        DistributorId distributorId = new DistributorId(UUID.fromString(id));

        // Verify distributor exists before deleting
        distributorRepository.findById(distributorId)
                .orElseThrow(() -> new NotFoundException("Distributor", id));

        distributorRepository.delete(distributorId);
    }
}

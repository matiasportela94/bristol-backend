package com.bristol.domain.distributor;

import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import lombok.RequiredArgsConstructor;

/**
 * Domain service for distributor approval logic.
 */
@RequiredArgsConstructor
public class DistributorApprovalService {

    private final DistributorRepository repository;
    private final TimeProvider timeProvider;

    /**
     * Approve a distributor registration.
     */
    public Distributor approveDistributor(DistributorId distributorId) {
        Distributor distributor = repository.findById(distributorId)
                .orElseThrow(() -> new ValidationException("Distributor not found"));

        if (!distributor.isPending()) {
            throw new ValidationException("Only pending distributors can be approved");
        }

        // Business rule: Check if CUIT is already registered by another distributor
        repository.findByCuit(distributor.getCuit()).ifPresent(existing -> {
            if (!existing.getId().equals(distributorId) && existing.isApproved()) {
                throw new ValidationException("CUIT is already registered by another approved distributor");
            }
        });

        Distributor approved = distributor.approve(timeProvider.now());
        return repository.save(approved);
    }

    /**
     * Reject a distributor registration.
     */
    public Distributor rejectDistributor(DistributorId distributorId) {
        Distributor distributor = repository.findById(distributorId)
                .orElseThrow(() -> new ValidationException("Distributor not found"));

        if (!distributor.isPending()) {
            throw new ValidationException("Only pending distributors can be rejected");
        }

        Distributor rejected = distributor.reject(timeProvider.now());
        return repository.save(rejected);
    }
}

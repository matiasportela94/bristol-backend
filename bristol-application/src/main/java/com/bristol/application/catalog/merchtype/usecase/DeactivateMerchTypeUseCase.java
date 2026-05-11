package com.bristol.application.catalog.merchtype.usecase;

import com.bristol.domain.catalog.MerchType;
import com.bristol.domain.catalog.MerchTypeId;
import com.bristol.domain.catalog.MerchTypeRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import com.bristol.domain.shared.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use case to deactivate a merch type.
 */
@Service
@RequiredArgsConstructor
public class DeactivateMerchTypeUseCase {

    private final MerchTypeRepository merchTypeRepository;
    private final TimeProvider timeProvider;

    @Transactional
    public void execute(UUID id) {
        // Find existing merch type
        MerchType merchType = merchTypeRepository.findById(new MerchTypeId(id))
                .orElseThrow(() -> new NotFoundException("MerchType", id.toString()));

        // Deactivate and save
        MerchType deactivated = merchType.deactivate(timeProvider.now());
        merchTypeRepository.save(deactivated);
    }
}

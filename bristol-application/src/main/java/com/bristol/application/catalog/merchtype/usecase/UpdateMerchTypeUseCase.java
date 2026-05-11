package com.bristol.application.catalog.merchtype.usecase;

import com.bristol.application.catalog.merchtype.dto.MerchTypeDto;
import com.bristol.application.catalog.merchtype.dto.UpdateMerchTypeRequest;
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
 * Use case to update a merch type.
 */
@Service
@RequiredArgsConstructor
public class UpdateMerchTypeUseCase {

    private final MerchTypeRepository merchTypeRepository;
    private final MerchTypeApplicationMapper mapper;
    private final TimeProvider timeProvider;

    @Transactional
    public MerchTypeDto execute(UUID id, UpdateMerchTypeRequest request) {
        // Find existing merch type
        MerchType merchType = merchTypeRepository.findById(new MerchTypeId(id))
                .orElseThrow(() -> new NotFoundException("MerchType", id.toString()));

        // Update
        MerchType updated = merchType.update(
                request.getName(),
                request.getDescription(),
                request.getCategory(),
                request.getDisplayOrder(),
                timeProvider.now()
        );

        // Save and return
        MerchType saved = merchTypeRepository.save(updated);
        return mapper.toDto(saved);
    }
}

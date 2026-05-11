package com.bristol.application.catalog.specialtype.usecase;

import com.bristol.application.catalog.specialtype.dto.SpecialTypeDto;
import com.bristol.application.catalog.specialtype.dto.UpdateSpecialTypeRequest;
import com.bristol.domain.catalog.SpecialType;
import com.bristol.domain.catalog.SpecialTypeId;
import com.bristol.domain.catalog.SpecialTypeRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import com.bristol.domain.shared.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use case to update a special type.
 */
@Service
@RequiredArgsConstructor
public class UpdateSpecialTypeUseCase {

    private final SpecialTypeRepository specialTypeRepository;
    private final SpecialTypeApplicationMapper mapper;
    private final TimeProvider timeProvider;

    @Transactional
    public SpecialTypeDto execute(UUID id, UpdateSpecialTypeRequest request) {
        // Find existing special type
        SpecialType specialType = specialTypeRepository.findById(new SpecialTypeId(id))
                .orElseThrow(() -> new NotFoundException("SpecialType", id.toString()));

        // Update
        SpecialType updated = specialType.update(
                request.getName(),
                request.getDescription(),
                request.isRequiresQuote(),
                request.getDisplayOrder(),
                timeProvider.now()
        );

        // Save and return
        SpecialType saved = specialTypeRepository.save(updated);
        return mapper.toDto(saved);
    }
}

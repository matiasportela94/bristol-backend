package com.bristol.application.catalog.specialtype.usecase;

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
 * Use case to deactivate a special type.
 */
@Service
@RequiredArgsConstructor
public class DeactivateSpecialTypeUseCase {

    private final SpecialTypeRepository specialTypeRepository;
    private final TimeProvider timeProvider;

    @Transactional
    public void execute(UUID id) {
        // Find existing special type
        SpecialType specialType = specialTypeRepository.findById(new SpecialTypeId(id))
                .orElseThrow(() -> new NotFoundException("SpecialType", id.toString()));

        // Deactivate and save
        SpecialType deactivated = specialType.deactivate(timeProvider.now());
        specialTypeRepository.save(deactivated);
    }
}

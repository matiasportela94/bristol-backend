package com.bristol.application.catalog.specialtype.usecase;

import com.bristol.application.catalog.specialtype.dto.CreateSpecialTypeRequest;
import com.bristol.application.catalog.specialtype.dto.SpecialTypeDto;
import com.bristol.domain.catalog.SpecialType;
import com.bristol.domain.catalog.SpecialTypeRepository;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case to create a new special type.
 */
@Service
@RequiredArgsConstructor
public class CreateSpecialTypeUseCase {

    private final SpecialTypeRepository specialTypeRepository;
    private final SpecialTypeApplicationMapper mapper;
    private final TimeProvider timeProvider;

    @Transactional
    public SpecialTypeDto execute(CreateSpecialTypeRequest request) {
        // Validate code uniqueness
        if (specialTypeRepository.existsByCode(request.getCode())) {
            throw new ValidationException("Special type with code '" + request.getCode() + "' already exists");
        }

        // Create special type
        SpecialType specialType = SpecialType.create(
                request.getCode(),
                request.getName(),
                request.getDescription(),
                request.isRequiresQuote(),
                timeProvider.now()
        );

        // Save and return
        SpecialType saved = specialTypeRepository.save(specialType);
        return mapper.toDto(saved);
    }
}

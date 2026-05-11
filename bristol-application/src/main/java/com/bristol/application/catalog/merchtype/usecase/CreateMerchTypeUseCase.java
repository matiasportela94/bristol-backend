package com.bristol.application.catalog.merchtype.usecase;

import com.bristol.application.catalog.merchtype.dto.CreateMerchTypeRequest;
import com.bristol.application.catalog.merchtype.dto.MerchTypeDto;
import com.bristol.domain.catalog.MerchType;
import com.bristol.domain.catalog.MerchTypeRepository;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case to create a new merch type.
 */
@Service
@RequiredArgsConstructor
public class CreateMerchTypeUseCase {

    private final MerchTypeRepository merchTypeRepository;
    private final MerchTypeApplicationMapper mapper;
    private final TimeProvider timeProvider;

    @Transactional
    public MerchTypeDto execute(CreateMerchTypeRequest request) {
        // Validate code uniqueness
        if (merchTypeRepository.existsByCode(request.getCode())) {
            throw new ValidationException("Merch type with code '" + request.getCode() + "' already exists");
        }

        // Create merch type
        MerchType merchType = MerchType.create(
                request.getCode(),
                request.getName(),
                request.getDescription(),
                request.getCategory(),
                timeProvider.now()
        );

        // Save and return
        MerchType saved = merchTypeRepository.save(merchType);
        return mapper.toDto(saved);
    }
}

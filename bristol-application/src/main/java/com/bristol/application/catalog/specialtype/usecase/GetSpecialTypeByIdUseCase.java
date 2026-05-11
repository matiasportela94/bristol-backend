package com.bristol.application.catalog.specialtype.usecase;

import com.bristol.application.catalog.specialtype.dto.SpecialTypeDto;
import com.bristol.domain.catalog.SpecialTypeId;
import com.bristol.domain.catalog.SpecialTypeRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use case to get a special type by ID.
 */
@Service
@RequiredArgsConstructor
public class GetSpecialTypeByIdUseCase {

    private final SpecialTypeRepository specialTypeRepository;
    private final SpecialTypeApplicationMapper mapper;

    @Transactional(readOnly = true)
    public SpecialTypeDto execute(UUID id) {
        return specialTypeRepository.findById(new SpecialTypeId(id))
                .map(mapper::toDto)
                .orElseThrow(() -> new NotFoundException("SpecialType", id.toString()));
    }
}

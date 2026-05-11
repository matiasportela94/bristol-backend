package com.bristol.application.catalog.specialtype.usecase;

import com.bristol.application.catalog.specialtype.dto.SpecialTypeDto;
import com.bristol.domain.catalog.SpecialTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use case to get all special types (including inactive).
 */
@Service
@RequiredArgsConstructor
public class GetAllSpecialTypesUseCase {

    private final SpecialTypeRepository specialTypeRepository;
    private final SpecialTypeApplicationMapper mapper;

    @Cacheable("specialTypes")
    @Transactional(readOnly = true)
    public List<SpecialTypeDto> execute() {
        return specialTypeRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}

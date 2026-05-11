package com.bristol.application.catalog.merchtype.usecase;

import com.bristol.application.catalog.merchtype.dto.MerchTypeDto;
import com.bristol.domain.catalog.MerchTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use case to get all active merch types.
 */
@Service
@RequiredArgsConstructor
public class GetActiveMerchTypesUseCase {

    private final MerchTypeRepository merchTypeRepository;
    private final MerchTypeApplicationMapper mapper;

    @Cacheable("merchTypes")
    @Transactional(readOnly = true)
    public List<MerchTypeDto> execute() {
        return merchTypeRepository.findActive().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}

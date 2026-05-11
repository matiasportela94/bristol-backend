package com.bristol.application.catalog.merchtype.usecase;

import com.bristol.application.catalog.merchtype.dto.MerchTypeDto;
import com.bristol.domain.catalog.MerchTypeId;
import com.bristol.domain.catalog.MerchTypeRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use case to get a merch type by ID.
 */
@Service
@RequiredArgsConstructor
public class GetMerchTypeByIdUseCase {

    private final MerchTypeRepository merchTypeRepository;
    private final MerchTypeApplicationMapper mapper;

    @Transactional(readOnly = true)
    public MerchTypeDto execute(UUID id) {
        return merchTypeRepository.findById(new MerchTypeId(id))
                .map(mapper::toDto)
                .orElseThrow(() -> new NotFoundException("MerchType", id.toString()));
    }
}

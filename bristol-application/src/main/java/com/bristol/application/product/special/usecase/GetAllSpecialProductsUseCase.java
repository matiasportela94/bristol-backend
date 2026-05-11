package com.bristol.application.product.special.usecase;

import com.bristol.application.product.special.dto.SpecialProductDto;
import com.bristol.domain.product.SpecialProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use case to get all special products.
 */
@Service
@RequiredArgsConstructor
public class GetAllSpecialProductsUseCase {

    private final SpecialProductRepository specialProductRepository;
    private final SpecialProductApplicationMapper mapper;

    @Transactional(readOnly = true)
    public List<SpecialProductDto> execute() {
        return specialProductRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}

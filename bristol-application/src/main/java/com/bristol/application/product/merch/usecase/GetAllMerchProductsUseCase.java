package com.bristol.application.product.merch.usecase;

import com.bristol.application.product.merch.dto.MerchProductDto;
import com.bristol.domain.product.MerchProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use case to get all merch products.
 */
@Service
@RequiredArgsConstructor
public class GetAllMerchProductsUseCase {

    private final MerchProductRepository merchProductRepository;
    private final MerchProductApplicationMapper mapper;

    @Transactional(readOnly = true)
    public List<MerchProductDto> execute() {
        return merchProductRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}

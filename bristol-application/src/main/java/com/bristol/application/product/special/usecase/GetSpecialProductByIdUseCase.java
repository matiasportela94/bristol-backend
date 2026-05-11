package com.bristol.application.product.special.usecase;

import com.bristol.application.product.special.dto.SpecialProductDto;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.SpecialProduct;
import com.bristol.domain.product.SpecialProductRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use case to get a special product by ID.
 */
@Service
@RequiredArgsConstructor
public class GetSpecialProductByIdUseCase {

    private final SpecialProductRepository specialProductRepository;
    private final SpecialProductApplicationMapper mapper;

    @Transactional(readOnly = true)
    public SpecialProductDto execute(String id) {
        ProductId productId = new ProductId(UUID.fromString(id));
        SpecialProduct product = specialProductRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("SpecialProduct", id));

        return mapper.toDto(product);
    }
}

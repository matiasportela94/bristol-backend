package com.bristol.application.product.merch.usecase;

import com.bristol.application.product.merch.dto.MerchProductDto;
import com.bristol.domain.product.MerchProduct;
import com.bristol.domain.product.MerchProductRepository;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use case to get a merch product by ID.
 */
@Service
@RequiredArgsConstructor
public class GetMerchProductByIdUseCase {

    private final MerchProductRepository merchProductRepository;
    private final MerchProductApplicationMapper mapper;

    @Transactional(readOnly = true)
    public MerchProductDto execute(String id) {
        ProductId productId = new ProductId(UUID.fromString(id));
        MerchProduct product = merchProductRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("MerchProduct", id));

        return mapper.toDto(product);
    }
}

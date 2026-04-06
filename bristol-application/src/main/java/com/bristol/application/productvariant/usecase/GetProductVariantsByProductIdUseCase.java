package com.bristol.application.productvariant.usecase;

import com.bristol.application.productvariant.dto.ProductVariantDto;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Use case to get all product variants for a specific product.
 */
@Service
@RequiredArgsConstructor
public class GetProductVariantsByProductIdUseCase {

    private final ProductVariantRepository productVariantRepository;
    private final ProductVariantMapper productVariantMapper;

    @Transactional(readOnly = true)
    public List<ProductVariantDto> execute(String productId) {
        ProductId id = new ProductId(UUID.fromString(productId));
        return productVariantRepository.findByProductId(id).stream()
                .map(productVariantMapper::toDto)
                .collect(Collectors.toList());
    }
}

package com.bristol.application.productvariant.usecase;

import com.bristol.application.productvariant.dto.ProductVariantDto;
import com.bristol.domain.product.ProductVariant;
import com.bristol.domain.product.ProductVariantId;
import com.bristol.domain.product.ProductVariantRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use case to get a product variant by ID.
 */
@Service
@RequiredArgsConstructor
public class GetProductVariantByIdUseCase {

    private final ProductVariantRepository productVariantRepository;
    private final ProductVariantMapper productVariantMapper;

    @Transactional(readOnly = true)
    public ProductVariantDto execute(String id) {
        ProductVariantId variantId = new ProductVariantId(UUID.fromString(id));
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new NotFoundException("ProductVariant", id));
        return productVariantMapper.toDto(variant);
    }
}

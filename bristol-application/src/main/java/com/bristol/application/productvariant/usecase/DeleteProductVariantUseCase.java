package com.bristol.application.productvariant.usecase;

import com.bristol.domain.product.ProductVariantId;
import com.bristol.domain.product.ProductVariantRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use case to delete a product variant.
 */
@Service
@RequiredArgsConstructor
public class DeleteProductVariantUseCase {

    private final ProductVariantRepository productVariantRepository;

    @Transactional
    public void execute(String id) {
        ProductVariantId variantId = new ProductVariantId(UUID.fromString(id));

        // Verify variant exists before deleting
        productVariantRepository.findById(variantId)
                .orElseThrow(() -> new NotFoundException("ProductVariant", id));

        productVariantRepository.delete(variantId);
    }
}

package com.bristol.application.productvariant.usecase;

import com.bristol.application.productvariant.dto.ProductVariantDto;
import com.bristol.application.productvariant.dto.UpdateProductVariantRequest;
import com.bristol.domain.product.ProductVariant;
import com.bristol.domain.product.ProductVariantId;
import com.bristol.domain.product.ProductVariantRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import com.bristol.domain.shared.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Use case to update a product variant.
 */
@Service
@RequiredArgsConstructor
public class UpdateProductVariantUseCase {

    private final ProductVariantRepository productVariantRepository;
    private final ProductVariantMapper productVariantMapper;

    @Transactional
    public ProductVariantDto execute(String id, UpdateProductVariantRequest request) {
        ProductVariantId variantId = new ProductVariantId(UUID.fromString(id));
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new NotFoundException("ProductVariant", id));

        // Build updated variant
        ProductVariant.ProductVariantBuilder builder = variant.toBuilder();

        if (request.getSku() != null) {
            builder.sku(request.getSku());
        }
        if (request.getSize() != null) {
            builder.size(request.getSize());
        }
        if (request.getColor() != null) {
            builder.color(request.getColor());
        }
        if (request.getAdditionalPrice() != null) {
            builder.additionalPrice(Money.of(request.getAdditionalPrice()));
        }
        if (request.getStockQuantity() != null) {
            builder.stockQuantity(request.getStockQuantity());
        }
        if (request.getImageUrl() != null) {
            builder.imageUrl(request.getImageUrl());
        }

        builder.updatedAt(Instant.now());

        ProductVariant updatedVariant = productVariantRepository.save(builder.build());
        return productVariantMapper.toDto(updatedVariant);
    }
}

package com.bristol.application.productvariant.usecase;

import com.bristol.application.product.service.StockSyncService;
import com.bristol.application.productvariant.dto.ProductVariantDto;
import com.bristol.application.productvariant.dto.UpdateProductVariantStockRequest;
import com.bristol.domain.product.ProductVariant;
import com.bristol.domain.product.ProductVariantId;
import com.bristol.domain.product.ProductVariantRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import com.bristol.domain.shared.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Use case to update product variant stock quantity.
 */
@Service
@RequiredArgsConstructor
public class UpdateProductVariantStockUseCase {

    private final ProductVariantRepository productVariantRepository;
    private final StockSyncService stockSyncService;
    private final ProductVariantMapper productVariantMapper;
    private final TimeProvider timeProvider;

    @Transactional
    public ProductVariantDto execute(String id, UpdateProductVariantStockRequest request) {
        ProductVariantId variantId = new ProductVariantId(UUID.fromString(id));
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new NotFoundException("ProductVariant", id));

        ProductVariant updatedVariant = variant.updateStock(request.getStockQuantity(), timeProvider.now());
        ProductVariant savedVariant = productVariantRepository.save(updatedVariant);
        stockSyncService.syncMerchStock(savedVariant.getProductId());
        return productVariantMapper.toDto(savedVariant);
    }
}

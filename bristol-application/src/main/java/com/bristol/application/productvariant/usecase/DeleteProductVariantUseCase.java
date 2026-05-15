package com.bristol.application.productvariant.usecase;

import com.bristol.application.product.service.StockSyncService;
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
    private final StockSyncService stockSyncService;

    @Transactional
    public void execute(String id) {
        ProductVariantId variantId = new ProductVariantId(UUID.fromString(id));

        var variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new NotFoundException("ProductVariant", id));

        var productId = variant.getProductId();
        productVariantRepository.delete(variantId);
        stockSyncService.syncMerchStock(productId);
    }
}

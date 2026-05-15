package com.bristol.application.productvariant.usecase;

import com.bristol.application.product.service.StockSyncService;
import com.bristol.application.productvariant.dto.CreateProductVariantRequest;
import com.bristol.application.productvariant.dto.ProductVariantDto;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductVariant;
import com.bristol.domain.product.ProductVariantRepository;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.shared.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Use case to create a new product variant.
 */
@Service
@RequiredArgsConstructor
public class CreateProductVariantUseCase {

    private final ProductVariantRepository productVariantRepository;
    private final StockSyncService stockSyncService;
    private final ProductVariantMapper productVariantMapper;
    private final TimeProvider timeProvider;

    @Transactional
    public ProductVariantDto execute(CreateProductVariantRequest request) {
        ProductId productId = new ProductId(UUID.fromString(request.getProductId()));
        Money additionalPrice = request.getAdditionalPrice() != null
                ? Money.of(request.getAdditionalPrice())
                : Money.zero();

        ProductVariant variant = ProductVariant.create(
                productId,
                request.getSku(),
                request.getSize(),
                request.getColor(),
                additionalPrice,
                request.getStockQuantity(),
                request.getImageUrl(),
                timeProvider.now()
        );

        ProductVariant savedVariant = productVariantRepository.save(variant);
        stockSyncService.syncMerchStock(productId);
        return productVariantMapper.toDto(savedVariant);
    }
}

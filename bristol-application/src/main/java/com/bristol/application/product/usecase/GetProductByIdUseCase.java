package com.bristol.application.product.usecase;

import com.bristol.application.product.dto.ProductDto;
import com.bristol.domain.product.BaseProduct;
import com.bristol.domain.product.ProductId;
import com.bristol.application.product.service.UnifiedProductService;
import com.bristol.domain.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case to retrieve a single product by ID.
 */
@Service
@RequiredArgsConstructor
public class GetProductByIdUseCase {

    private final UnifiedProductService unifiedProductService;
    private final UnifiedProductMapper unifiedProductMapper;
    private final ProductImageService productImageService;
    private final ProductVariantCatalogService productVariantCatalogService;
    private final ProductCatalogPromotionService productCatalogPromotionService;

    @Transactional(readOnly = true)
    public ProductDto execute(String productId) {
        ProductId id = new ProductId(productId);
        BaseProduct product = unifiedProductService.findById(id)
                .orElseThrow(() -> new NotFoundException("Product", productId));
        var images = productImageService.getImages(id);
        var variants = productVariantCatalogService.getVariants(id);
        return unifiedProductMapper.toDto(
                product,
                variants,
                productImageService.toDtos(images),
                productImageService.toPrimaryImageDataUrl(images),
                productCatalogPromotionService.resolveForProduct(product)
        );
    }
}

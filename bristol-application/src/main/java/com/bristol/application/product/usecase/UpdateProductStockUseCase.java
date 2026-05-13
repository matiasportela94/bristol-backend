package com.bristol.application.product.usecase;

import com.bristol.application.product.dto.ProductDto;
import com.bristol.application.product.dto.UpdateProductStockRequest;
import com.bristol.application.product.service.UnifiedProductService;
import com.bristol.domain.product.BaseProduct;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateProductStockUseCase {

    private final UnifiedProductService unifiedProductService;
    private final UnifiedProductMapper unifiedProductMapper;
    private final ProductImageService productImageService;
    private final ProductVariantCatalogService productVariantCatalogService;
    private final TimeProvider timeProvider;

    @Transactional
    public ProductDto execute(String productId, UpdateProductStockRequest request) {
        ProductId id = new ProductId(productId);

        BaseProduct product = unifiedProductService.findById(id)
                .orElseThrow(() -> new ValidationException("Product not found: " + productId));

        BaseProduct saved = unifiedProductService.save(product.updateStock(request.getStockQuantity(), timeProvider.now()));
        var images = productImageService.getImages(saved.getId());
        return unifiedProductMapper.toDto(
                saved,
                productVariantCatalogService.getVariants(saved.getId()),
                productImageService.toDtos(images),
                productImageService.toPrimaryImageDataUrl(images)
        );
    }
}

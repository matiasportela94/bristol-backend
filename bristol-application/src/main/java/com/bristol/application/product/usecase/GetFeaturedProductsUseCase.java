package com.bristol.application.product.usecase;

import com.bristol.application.product.dto.ProductDto;
import com.bristol.domain.product.BaseProduct;
import com.bristol.application.product.service.UnifiedProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use case to retrieve featured products.
 */
@Service
@RequiredArgsConstructor
public class GetFeaturedProductsUseCase {

    private final UnifiedProductService unifiedProductService;
    private final UnifiedProductMapper unifiedProductMapper;
    private final ProductImageService productImageService;
    private final ProductVariantCatalogService productVariantCatalogService;
    private final ProductCatalogPromotionService productCatalogPromotionService;

    @Cacheable("featuredProducts")
    @Transactional(readOnly = true)
    public List<ProductDto> execute() {
        List<BaseProduct> products = unifiedProductService.findFeatured();
        var promotionsByProductId = productCatalogPromotionService.resolveForProducts(products);
        return products.stream()
                .map(product -> {
                    var images = productImageService.getImages(product.getId());
                    var variants = productVariantCatalogService.getVariants(product.getId());
                    return unifiedProductMapper.toDto(
                            product,
                            variants,
                            productImageService.toDtos(images),
                            productImageService.toPrimaryImageDataUrl(images),
                            promotionsByProductId.getOrDefault(product.getId().getValue().toString(), List.of())
                    );
                })
                .collect(Collectors.toList());
    }
}

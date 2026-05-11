package com.bristol.application.product.usecase;

import com.bristol.application.product.dto.ProductDto;
import com.bristol.application.shared.dto.PagedResponse;
import com.bristol.domain.product.BaseProduct;
import com.bristol.domain.product.ProductCategory;
import com.bristol.application.product.service.UnifiedProductService;
import com.bristol.domain.shared.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use case to get products by category with pagination.
 */
@Service
@RequiredArgsConstructor
public class GetProductsByCategoryPaginatedUseCase {

    private final UnifiedProductService unifiedProductService;
    private final UnifiedProductMapper unifiedProductMapper;
    private final ProductImageService productImageService;
    private final ProductVariantCatalogService productVariantCatalogService;
    private final ProductCatalogPromotionService productCatalogPromotionService;

    @Transactional(readOnly = true)
    public PagedResponse<ProductDto> execute(ProductCategory category, int page, int size) {
        Page<BaseProduct> productsPage = unifiedProductService.findByCategoryPaginated(category, page, size);
        List<BaseProduct> products = productsPage.getContent();
        var promotionsByProductId = productCatalogPromotionService.resolveForProducts(products);

        List<ProductDto> productDtos = products.stream()
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

        return PagedResponse.of(productDtos, page, size, productsPage.getTotalElements());
    }
}

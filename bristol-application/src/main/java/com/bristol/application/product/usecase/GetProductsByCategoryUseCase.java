package com.bristol.application.product.usecase;

import com.bristol.application.product.dto.ProductDto;
import com.bristol.domain.product.Product;
import com.bristol.domain.product.ProductCategory;
import com.bristol.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use case to retrieve products by category.
 */
@Service
@RequiredArgsConstructor
public class GetProductsByCategoryUseCase {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductImageService productImageService;
    private final ProductVariantCatalogService productVariantCatalogService;
    private final ProductCatalogPromotionService productCatalogPromotionService;

    @Transactional(readOnly = true)
    public List<ProductDto> execute(ProductCategory category) {
        List<Product> products = productRepository.findByCategory(category);
        var promotionsByProductId = productCatalogPromotionService.resolveForProducts(products);
        return products.stream()
                .map(product -> {
                    var images = productImageService.getImages(product.getId());
                    var variants = productVariantCatalogService.getVariants(product.getId());
                    return productMapper.toDto(
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

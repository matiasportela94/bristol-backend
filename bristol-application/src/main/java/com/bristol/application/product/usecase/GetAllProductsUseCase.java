package com.bristol.application.product.usecase;

import com.bristol.application.product.dto.ProductDto;
import com.bristol.domain.product.Product;
import com.bristol.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use case to retrieve all products.
 */
@Service
@RequiredArgsConstructor
public class GetAllProductsUseCase {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductImageService productImageService;
    private final ProductVariantCatalogService productVariantCatalogService;
    private final ProductCatalogPromotionService productCatalogPromotionService;

    @Transactional(readOnly = true)
    public List<ProductDto> execute() {
        List<Product> products = productRepository.findAll();
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

package com.bristol.application.product.usecase;

import com.bristol.application.product.dto.ProductDto;
import com.bristol.domain.product.Product;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductRepository;
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

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductImageService productImageService;
    private final ProductVariantCatalogService productVariantCatalogService;
    private final ProductCatalogPromotionService productCatalogPromotionService;

    @Transactional(readOnly = true)
    public ProductDto execute(String productId) {
        ProductId id = new ProductId(productId);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product", productId));
        var images = productImageService.getImages(id);
        var variants = productVariantCatalogService.getVariants(id);
        return productMapper.toDto(
                product,
                variants,
                productImageService.toDtos(images),
                productImageService.toPrimaryImageDataUrl(images),
                productCatalogPromotionService.resolveForProduct(product)
        );
    }
}

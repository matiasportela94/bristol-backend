package com.bristol.application.product.usecase;

import com.bristol.application.product.dto.ProductDto;
import com.bristol.application.product.dto.UpdateProductStockRequest;
import com.bristol.domain.product.Product;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductRepository;
import com.bristol.domain.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UpdateProductStockUseCase {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductImageService productImageService;
    private final ProductVariantCatalogService productVariantCatalogService;

    @Transactional
    public ProductDto execute(String productId, UpdateProductStockRequest request) {
        ProductId id = new ProductId(productId);
        Instant now = Instant.now();

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Product not found: " + productId));

        Product updatedProduct = product.updateStock(request.getStockQuantity(), now);

        Product savedProduct = productRepository.save(updatedProduct);
        var images = productImageService.getImages(savedProduct.getId());
        return productMapper.toDto(
                savedProduct,
                productVariantCatalogService.getVariants(savedProduct.getId()),
                productImageService.toDtos(images),
                productImageService.toPrimaryImageDataUrl(images)
        );
    }
}

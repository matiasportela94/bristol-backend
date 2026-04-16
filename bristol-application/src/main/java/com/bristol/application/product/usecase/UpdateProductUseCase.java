package com.bristol.application.product.usecase;

import com.bristol.application.product.dto.ProductDto;
import com.bristol.application.product.dto.UpdateProductRequest;
import com.bristol.domain.product.Product;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.shared.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Use case to update an existing product.
 */
@Service
@RequiredArgsConstructor
public class UpdateProductUseCase {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductImageService productImageService;
    private final ProductVariantCatalogService productVariantCatalogService;
    private final TimeProvider timeProvider;

    @Transactional
    public ProductDto execute(String productId, UpdateProductRequest request) {
        ProductId id = new ProductId(productId);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product", productId));

        Product updatedProduct = product.update(
                request.getName(),
                request.getDescription(),
                request.getCategory(),
                request.getSubcategory(),
                request.getBeerType(),
                toMoney(request.getPrice()),
                timeProvider.now()
        );

        if (request.getIsFeatured() != null) {
            updatedProduct = request.getIsFeatured()
                    ? updatedProduct.setAsFeatured(timeProvider.now())
                    : updatedProduct.unsetAsFeatured(timeProvider.now());
        }

        Product saved = productRepository.save(updatedProduct);
        var images = productImageService.replaceImages(saved.getId(), request.getImages());
        return productMapper.toDto(
                saved,
                productVariantCatalogService.getVariants(saved.getId()),
                productImageService.toDtos(images),
                productImageService.toPrimaryImageDataUrl(images)
        );
    }

    private Money toMoney(BigDecimal amount) {
        return amount != null ? Money.of(amount) : null;
    }
}

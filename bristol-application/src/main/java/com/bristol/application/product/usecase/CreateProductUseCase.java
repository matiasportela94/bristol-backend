package com.bristol.application.product.usecase;

import com.bristol.application.product.dto.CreateProductRequest;
import com.bristol.application.product.dto.ProductDto;
import com.bristol.domain.product.Product;
import com.bristol.domain.product.ProductRepository;
import com.bristol.domain.shared.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Use case to create a new product.
 */
@Service
@RequiredArgsConstructor
public class CreateProductUseCase {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductImageService productImageService;

    @Transactional
    public ProductDto execute(CreateProductRequest request) {
        Product product = Product.create(
                request.getName(),
                request.getDescription(),
                request.getCategory(),
                request.getSubcategory(),
                request.getBeerType(),
                toMoney(request.getPrice()),
                request.getStockQuantity(),
                request.getLowStockThreshold(),
                Instant.now()
        );

        if (Boolean.TRUE.equals(request.getIsFeatured())) {
            product = product.setAsFeatured(Instant.now());
        }

        Product savedProduct = productRepository.save(product);
        var images = productImageService.createImages(savedProduct.getId(), request.getImages());
        return productMapper.toDto(
                savedProduct,
                productImageService.toDtos(images),
                productImageService.toPrimaryImageDataUrl(images)
        );
    }

    private Money toMoney(BigDecimal amount) {
        return amount != null ? Money.of(amount) : null;
    }
}

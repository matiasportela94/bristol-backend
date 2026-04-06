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

    @Transactional(readOnly = true)
    public List<ProductDto> execute(ProductCategory category) {
        List<Product> products = productRepository.findByCategory(category);
        return products.stream()
                .map(product -> {
                    var images = productImageService.getImages(product.getId());
                    return productMapper.toDto(
                            product,
                            productImageService.toDtos(images),
                            productImageService.toPrimaryImageDataUrl(images)
                    );
                })
                .collect(Collectors.toList());
    }
}

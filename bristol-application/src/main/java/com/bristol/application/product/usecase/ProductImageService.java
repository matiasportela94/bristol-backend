package com.bristol.application.product.usecase;

import com.bristol.application.product.dto.ProductImageDto;
import com.bristol.application.product.dto.ProductImageRequest;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductImage;
import com.bristol.domain.product.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Coordinates product image persistence and DTO mapping.
 */
@Component
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductImagePayloadMapper productImagePayloadMapper;

    public List<ProductImage> getImages(ProductId productId) {
        return productImageRepository.findByProductId(productId);
    }

    public List<ProductImage> createImages(ProductId productId, List<ProductImageRequest> imageRequests) {
        if (imageRequests == null || imageRequests.isEmpty()) {
            return List.of();
        }

        return saveAll(productId, imageRequests);
    }

    public List<ProductImage> replaceImages(ProductId productId, List<ProductImageRequest> imageRequests) {
        if (imageRequests == null) {
            return getImages(productId);
        }

        productImageRepository.deleteAllByProductId(productId);
        if (imageRequests.isEmpty()) {
            return List.of();
        }

        return saveAll(productId, imageRequests);
    }

    public List<ProductImageDto> toDtos(List<ProductImage> images) {
        return productImagePayloadMapper.toDtoList(images);
    }

    public String toPrimaryImageDataUrl(List<ProductImage> images) {
        return productImagePayloadMapper.toPrimaryImageDataUrl(images);
    }

    private List<ProductImage> saveAll(ProductId productId, List<ProductImageRequest> imageRequests) {
        return productImagePayloadMapper.toDomainList(productId, imageRequests).stream()
                .map(productImageRepository::save)
                .toList();
    }
}

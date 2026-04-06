package com.bristol.infrastructure.persistence.mapper;

import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductImage;
import com.bristol.domain.product.ProductImageId;
import com.bristol.infrastructure.persistence.entity.ProductImageEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Maps product image domain objects to JPA entities.
 */
@Component
public class ProductImageMapper {

    public ProductImage toDomain(ProductImageEntity entity) {
        return ProductImage.builder()
                .id(new ProductImageId(entity.getId()))
                .productId(new ProductId(entity.getProductId()))
                .imageData(entity.getImageData() != null ? Arrays.copyOf(entity.getImageData(), entity.getImageData().length) : null)
                .contentType(entity.getContentType())
                .fileName(entity.getFileName())
                .displayOrder(entity.getDisplayOrder())
                .isPrimary(Boolean.TRUE.equals(entity.getIsPrimary()))
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public ProductImageEntity toEntity(ProductImage domain) {
        return ProductImageEntity.builder()
                .id(domain.getId().getValue())
                .productId(domain.getProductId().getValue())
                .imageData(domain.getImageData())
                .contentType(domain.getContentType())
                .fileName(domain.getFileName())
                .displayOrder(domain.getDisplayOrder())
                .isPrimary(domain.isPrimary())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}

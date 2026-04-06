package com.bristol.application.productvariant.usecase;

import com.bristol.application.productvariant.dto.ProductVariantDto;
import com.bristol.domain.product.ProductVariant;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert between ProductVariant domain entity and ProductVariantDto.
 */
@Component
public class ProductVariantMapper {

    public ProductVariantDto toDto(ProductVariant variant) {
        return ProductVariantDto.builder()
                .id(variant.getId().getValue().toString())
                .productId(variant.getProductId().getValue().toString())
                .sku(variant.getSku())
                .size(variant.getSize())
                .color(variant.getColor())
                .additionalPrice(variant.getAdditionalPrice().getAmount())
                .stockQuantity(variant.getStockQuantity())
                .imageUrl(variant.getImageUrl())
                .inStock(variant.isInStock())
                .build();
    }
}

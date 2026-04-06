package com.bristol.application.product.usecase;

import com.bristol.application.product.dto.ProductImageDto;
import com.bristol.application.product.dto.ProductDto;
import com.bristol.domain.product.Product;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper to convert between Product domain entity and ProductDto.
 */
@Component
public class ProductMapper {

    public ProductDto toDto(Product product) {
        return toDto(product, List.of(), null);
    }

    public ProductDto toDto(Product product, List<ProductImageDto> images, String primaryImageDataUrl) {
        return ProductDto.builder()
                .id(product.getId().getValue().toString())
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .subcategory(product.getSubcategory())
                .beerType(product.getBeerType())
                .price(product.getBasePrice() != null ? product.getBasePrice().getAmount() : null)
                .stockQuantity(product.getStockQuantity())
                .minStockLevel(product.getLowStockThreshold())
                .images(images)
                .active(!product.isDeleted())
                .featured(product.isFeatured())
                .averageRating(null) // TODO: Reviews not aggregated in domain yet
                .reviewCount(null) // TODO: Reviews not aggregated in domain yet
                .build();
    }
}

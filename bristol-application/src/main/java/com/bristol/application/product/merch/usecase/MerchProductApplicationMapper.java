package com.bristol.application.product.merch.usecase;

import com.bristol.application.product.merch.dto.MerchProductDto;
import com.bristol.domain.product.MerchProduct;
import org.springframework.stereotype.Component;

/**
 * Mapper between MerchProduct domain and DTO.
 */
@Component
public class MerchProductApplicationMapper {

    public MerchProductDto toDto(MerchProduct merchProduct) {
        if (merchProduct == null) {
            return null;
        }

        return MerchProductDto.builder()
                .id(merchProduct.getId().getValue().toString())
                .name(merchProduct.getName())
                .description(merchProduct.getDescription())
                .basePrice(merchProduct.getBasePrice() != null ? merchProduct.getBasePrice().getAmount() : null)
                .merchTypeId(merchProduct.getMerchTypeId() != null ? merchProduct.getMerchTypeId().getValue().toString() : null)
                .merchCategory(merchProduct.getMerchCategory())
                .material(merchProduct.getMaterial())
                .brand(merchProduct.getBrand())
                .stockQuantity(merchProduct.getStockQuantity())
                .lowStockThreshold(merchProduct.getLowStockThreshold())
                .featured(merchProduct.isFeatured())
                .createdAt(merchProduct.getCreatedAt())
                .updatedAt(merchProduct.getUpdatedAt())
                .build();
    }
}

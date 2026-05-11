package com.bristol.application.product.special.usecase;

import com.bristol.application.product.special.dto.SpecialProductDto;
import com.bristol.domain.product.SpecialProduct;
import org.springframework.stereotype.Component;

/**
 * Mapper between SpecialProduct domain and DTO.
 */
@Component
public class SpecialProductApplicationMapper {

    public SpecialProductDto toDto(SpecialProduct specialProduct) {
        if (specialProduct == null) {
            return null;
        }

        return SpecialProductDto.builder()
                .id(specialProduct.getId().getValue().toString())
                .name(specialProduct.getName())
                .description(specialProduct.getDescription())
                .basePrice(specialProduct.getBasePrice() != null ? specialProduct.getBasePrice().getAmount() : null)
                .specialTypeId(specialProduct.getSpecialTypeId() != null ? specialProduct.getSpecialTypeId().getValue().toString() : null)
                .notes(specialProduct.getNotes())
                .requiresQuote(specialProduct.isRequiresQuote())
                .stockQuantity(specialProduct.getStockQuantity())
                .lowStockThreshold(specialProduct.getLowStockThreshold())
                .featured(specialProduct.isFeatured())
                .createdAt(specialProduct.getCreatedAt())
                .updatedAt(specialProduct.getUpdatedAt())
                .build();
    }
}

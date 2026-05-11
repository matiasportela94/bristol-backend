package com.bristol.infrastructure.persistence.mapper;

import com.bristol.domain.catalog.SpecialTypeId;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.SpecialProduct;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.infrastructure.persistence.entity.SpecialProductEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper for SpecialProduct domain object and SpecialProductEntity.
 */
@Component
public class SpecialProductMapper {

    public SpecialProduct toDomain(SpecialProductEntity entity) {
        if (entity == null) {
            return null;
        }

        return SpecialProduct.builder()
                .id(new ProductId(entity.getId()))
                .name(entity.getName())
                .description(entity.getDescription())
                .basePrice(entity.getBasePrice() != null ? Money.of(entity.getBasePrice()) : null)
                .specialTypeId(entity.getSpecialTypeId() != null ? new SpecialTypeId(entity.getSpecialTypeId()) : null)
                .notes(entity.getNotes())
                .requiresQuote(entity.getRequiresQuote() != null ? entity.getRequiresQuote() : false)
                .stockQuantity(entity.getStockQuantity())
                .lowStockThreshold(entity.getLowStockThreshold())
                .featured(entity.getIsFeatured() != null ? entity.getIsFeatured() : false)
                .deletedAt(entity.getDeletedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public SpecialProductEntity toEntity(SpecialProduct domain) {
        if (domain == null) {
            return null;
        }

        return SpecialProductEntity.builder()
                .id(domain.getId().getValue())
                .name(domain.getName())
                .description(domain.getDescription())
                .basePrice(domain.getBasePrice() != null ? domain.getBasePrice().getAmount() : null)
                .specialTypeId(domain.getSpecialTypeId() != null ? domain.getSpecialTypeId().getValue() : null)
                .notes(domain.getNotes())
                .requiresQuote(domain.isRequiresQuote())
                .stockQuantity(domain.getStockQuantity())
                .lowStockThreshold(domain.getLowStockThreshold())
                .isFeatured(domain.isFeatured())
                .deletedAt(domain.getDeletedAt())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}

package com.bristol.infrastructure.persistence.mapper;

import com.bristol.domain.catalog.MerchCategory;
import com.bristol.domain.catalog.MerchTypeId;
import com.bristol.domain.product.MerchProduct;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.infrastructure.persistence.entity.MerchProductEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper for MerchProduct domain object and MerchProductEntity.
 */
@Component
public class MerchProductMapper {

    public MerchProduct toDomain(MerchProductEntity entity) {
        if (entity == null) {
            return null;
        }

        return MerchProduct.builder()
                .id(new ProductId(entity.getId()))
                .name(entity.getName())
                .description(entity.getDescription())
                .basePrice(entity.getBasePrice() != null ? Money.of(entity.getBasePrice()) : null)
                .merchTypeId(entity.getMerchTypeId() != null ? new MerchTypeId(entity.getMerchTypeId()) : null)
                .merchCategory(entity.getMerchCategory() != null ? MerchCategory.valueOf(entity.getMerchCategory().name()) : null)
                .material(entity.getMaterial())
                .brand(entity.getBrand())
                .stockQuantity(entity.getStockQuantity())
                .lowStockThreshold(entity.getLowStockThreshold())
                .featured(entity.getIsFeatured() != null ? entity.getIsFeatured() : false)
                .deletedAt(entity.getDeletedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public MerchProductEntity toEntity(MerchProduct domain) {
        if (domain == null) {
            return null;
        }

        return MerchProductEntity.builder()
                .id(domain.getId().getValue())
                .name(domain.getName())
                .description(domain.getDescription())
                .basePrice(domain.getBasePrice() != null ? domain.getBasePrice().getAmount() : null)
                .merchTypeId(domain.getMerchTypeId() != null ? domain.getMerchTypeId().getValue() : null)
                .merchCategory(domain.getMerchCategory() != null ?
                    MerchProductEntity.MerchCategoryEnum.valueOf(domain.getMerchCategory().name()) : null)
                .material(domain.getMaterial())
                .brand(domain.getBrand())
                .stockQuantity(domain.getStockQuantity())
                .lowStockThreshold(domain.getLowStockThreshold())
                .isFeatured(domain.isFeatured())
                .deletedAt(domain.getDeletedAt())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}

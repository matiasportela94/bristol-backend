package com.bristol.infrastructure.persistence.mapper;

import com.bristol.domain.catalog.MerchCategory;
import com.bristol.domain.catalog.MerchType;
import com.bristol.domain.catalog.MerchTypeId;
import com.bristol.infrastructure.persistence.entity.MerchTypeEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper for MerchType domain object and MerchTypeEntity.
 */
@Component
public class MerchTypeMapper {

    public MerchType toDomain(MerchTypeEntity entity) {
        if (entity == null) {
            return null;
        }

        return MerchType.builder()
                .id(new MerchTypeId(entity.getId()))
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .category(toDomainCategory(entity.getCategory()))
                .active(entity.isActive())
                .displayOrder(entity.getDisplayOrder())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public MerchTypeEntity toEntity(MerchType domain) {
        if (domain == null) {
            return null;
        }

        return MerchTypeEntity.builder()
                .id(domain.getId().getValue())
                .code(domain.getCode())
                .name(domain.getName())
                .description(domain.getDescription())
                .category(toEntityCategory(domain.getCategory()))
                .active(domain.isActive())
                .displayOrder(domain.getDisplayOrder())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    private MerchCategory toDomainCategory(MerchTypeEntity.MerchCategoryEnum entityCategory) {
        if (entityCategory == null) {
            return null;
        }
        return MerchCategory.valueOf(entityCategory.name());
    }

    private MerchTypeEntity.MerchCategoryEnum toEntityCategory(MerchCategory domainCategory) {
        if (domainCategory == null) {
            return null;
        }
        return MerchTypeEntity.MerchCategoryEnum.valueOf(domainCategory.name());
    }
}

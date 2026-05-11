package com.bristol.infrastructure.persistence.mapper;

import com.bristol.domain.catalog.SpecialType;
import com.bristol.domain.catalog.SpecialTypeId;
import com.bristol.infrastructure.persistence.entity.SpecialTypeEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper for SpecialType domain object and SpecialTypeEntity.
 */
@Component
public class SpecialTypeMapper {

    public SpecialType toDomain(SpecialTypeEntity entity) {
        if (entity == null) {
            return null;
        }

        return SpecialType.builder()
                .id(new SpecialTypeId(entity.getId()))
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .requiresQuote(entity.isRequiresQuote())
                .active(entity.isActive())
                .displayOrder(entity.getDisplayOrder())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public SpecialTypeEntity toEntity(SpecialType domain) {
        if (domain == null) {
            return null;
        }

        return SpecialTypeEntity.builder()
                .id(domain.getId().getValue())
                .code(domain.getCode())
                .name(domain.getName())
                .description(domain.getDescription())
                .requiresQuote(domain.isRequiresQuote())
                .active(domain.isActive())
                .displayOrder(domain.getDisplayOrder())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}

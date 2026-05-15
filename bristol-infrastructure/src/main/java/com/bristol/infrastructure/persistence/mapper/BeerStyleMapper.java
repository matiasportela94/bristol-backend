package com.bristol.infrastructure.persistence.mapper;

import com.bristol.domain.catalog.BeerStyle;
import com.bristol.domain.catalog.BeerStyleCategory;
import com.bristol.domain.catalog.BeerStyleId;
import com.bristol.infrastructure.persistence.entity.BeerStyleEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper for BeerStyle domain object and BeerStyleEntity.
 */
@Component
public class BeerStyleMapper {

    public BeerStyle toDomain(BeerStyleEntity entity) {
        if (entity == null) {
            return null;
        }

        return BeerStyle.builder()
                .id(new BeerStyleId(entity.getId()))
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .category(toDomainCategory(entity.getCategory()))
                .active(entity.isActive())
                .displayOrder(entity.getDisplayOrder())
                .imageData(entity.getImageData())
                .imageContentType(entity.getImageContentType())
                .imageFileName(entity.getImageFileName())
                .abv(entity.getAbv())
                .ibu(entity.getIbu())
                .srm(entity.getSrm())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public BeerStyleEntity toEntity(BeerStyle domain) {
        if (domain == null) {
            return null;
        }

        return BeerStyleEntity.builder()
                .id(domain.getId().getValue())
                .code(domain.getCode())
                .name(domain.getName())
                .description(domain.getDescription())
                .category(toEntityCategory(domain.getCategory()))
                .active(domain.isActive())
                .displayOrder(domain.getDisplayOrder())
                .imageData(domain.getImageData())
                .imageContentType(domain.getImageContentType())
                .imageFileName(domain.getImageFileName())
                .abv(domain.getAbv())
                .ibu(domain.getIbu())
                .srm(domain.getSrm())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    private BeerStyleCategory toDomainCategory(BeerStyleEntity.BeerStyleCategoryEnum entityCategory) {
        if (entityCategory == null) {
            return null;
        }
        return BeerStyleCategory.valueOf(entityCategory.name());
    }

    private BeerStyleEntity.BeerStyleCategoryEnum toEntityCategory(BeerStyleCategory domainCategory) {
        if (domainCategory == null) {
            return null;
        }
        return BeerStyleEntity.BeerStyleCategoryEnum.valueOf(domainCategory.name());
    }
}

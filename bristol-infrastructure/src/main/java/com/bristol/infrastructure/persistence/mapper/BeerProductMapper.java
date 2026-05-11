package com.bristol.infrastructure.persistence.mapper;

import com.bristol.domain.catalog.BeerStyleCategory;
import com.bristol.domain.catalog.BeerStyleId;
import com.bristol.domain.product.BeerProduct;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.infrastructure.persistence.entity.BeerProductEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper for BeerProduct domain object and BeerProductEntity.
 */
@Component
public class BeerProductMapper {

    public BeerProduct toDomain(BeerProductEntity entity) {
        if (entity == null) {
            return null;
        }

        return BeerProduct.builder()
                .id(new ProductId(entity.getId()))
                .name(entity.getName())
                .description(entity.getDescription())
                .basePrice(entity.getBasePrice() != null ? Money.of(entity.getBasePrice()) : null)
                .beerStyleId(entity.getBeerStyleId() != null ? new BeerStyleId(entity.getBeerStyleId()) : null)
                .beerCategory(entity.getBeerCategory() != null ? BeerStyleCategory.valueOf(entity.getBeerCategory().name()) : null)
                .abv(entity.getAbv())
                .ibu(entity.getIbu())
                .srm(entity.getSrm())
                .origin(entity.getOrigin())
                .brewery(entity.getBrewery())
                .stockQuantity(entity.getStockQuantity())
                .lowStockThreshold(entity.getLowStockThreshold())
                .featured(entity.getIsFeatured() != null ? entity.getIsFeatured() : false)
                .deletedAt(entity.getDeletedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public BeerProductEntity toEntity(BeerProduct domain) {
        if (domain == null) {
            return null;
        }

        return BeerProductEntity.builder()
                .id(domain.getId().getValue())
                .name(domain.getName())
                .description(domain.getDescription())
                .basePrice(domain.getBasePrice() != null ? domain.getBasePrice().getAmount() : null)
                .beerStyleId(domain.getBeerStyleId() != null ? domain.getBeerStyleId().getValue() : null)
                .beerCategory(domain.getBeerCategory() != null ?
                    BeerProductEntity.BeerCategoryEnum.valueOf(domain.getBeerCategory().name()) : null)
                .abv(domain.getAbv())
                .ibu(domain.getIbu() != null ? domain.getIbu().intValue() : null)
                .srm(domain.getSrm() != null ? domain.getSrm().intValue() : null)
                .origin(domain.getOrigin())
                .brewery(domain.getBrewery())
                .stockQuantity(domain.getStockQuantity())
                .lowStockThreshold(domain.getLowStockThreshold())
                .isFeatured(domain.isFeatured())
                .deletedAt(domain.getDeletedAt())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}

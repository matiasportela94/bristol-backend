package com.bristol.infrastructure.persistence.mapper;

import com.bristol.domain.brewery.BreweryInventory;
import com.bristol.domain.brewery.BreweryInventoryId;
import com.bristol.domain.catalog.BeerStyleId;
import com.bristol.infrastructure.persistence.entity.BreweryInventoryEntity;
import org.springframework.stereotype.Component;

@Component
public class BreweryInventoryMapper {

    public BreweryInventory toDomain(BreweryInventoryEntity entity) {
        if (entity == null) return null;
        return BreweryInventory.builder()
                .id(new BreweryInventoryId(entity.getId()))
                .beerStyleId(new BeerStyleId(entity.getBeerStyleId()))
                .totalCans(entity.getTotalCans())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public BreweryInventoryEntity toEntity(BreweryInventory domain) {
        if (domain == null) return null;
        return BreweryInventoryEntity.builder()
                .id(domain.getId().getValue())
                .beerStyleId(domain.getBeerStyleId().getValue())
                .totalCans(domain.getTotalCans())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}

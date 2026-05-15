package com.bristol.infrastructure.persistence.mapper;

import com.bristol.domain.brewery.BreweryBatch;
import com.bristol.domain.brewery.BreweryBatchId;
import com.bristol.domain.catalog.BeerStyleId;
import com.bristol.infrastructure.persistence.entity.BreweryBatchEntity;
import org.springframework.stereotype.Component;

@Component
public class BreweryBatchMapper {

    public BreweryBatch toDomain(BreweryBatchEntity entity) {
        if (entity == null) return null;
        return BreweryBatch.builder()
                .id(new BreweryBatchId(entity.getId()))
                .beerStyleId(new BeerStyleId(entity.getBeerStyleId()))
                .cansProduced(entity.getCansProduced())
                .canCapacityMl(entity.getCanCapacityMl())
                .costPerCan(entity.getCostPerCan())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public BreweryBatchEntity toEntity(BreweryBatch domain) {
        if (domain == null) return null;
        return BreweryBatchEntity.builder()
                .id(domain.getId().getValue())
                .beerStyleId(domain.getBeerStyleId().getValue())
                .cansProduced(domain.getCansProduced())
                .canCapacityMl(domain.getCanCapacityMl())
                .costPerCan(domain.getCostPerCan())
                .notes(domain.getNotes())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}

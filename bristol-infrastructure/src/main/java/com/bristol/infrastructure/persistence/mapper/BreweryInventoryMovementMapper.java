package com.bristol.infrastructure.persistence.mapper;

import com.bristol.domain.brewery.BreweryInventoryMovement;
import com.bristol.domain.brewery.BreweryInventoryMovementId;
import com.bristol.domain.catalog.BeerStyleId;
import com.bristol.infrastructure.persistence.entity.BreweryInventoryMovementEntity;
import org.springframework.stereotype.Component;

@Component
public class BreweryInventoryMovementMapper {

    public BreweryInventoryMovement toDomain(BreweryInventoryMovementEntity entity) {
        if (entity == null) return null;
        return BreweryInventoryMovement.builder()
                .id(new BreweryInventoryMovementId(entity.getId()))
                .beerStyleId(new BeerStyleId(entity.getBeerStyleId()))
                .type(entity.getType())
                .cansDelta(entity.getCansDelta())
                .cansBefore(entity.getCansBefore())
                .cansAfter(entity.getCansAfter())
                .referenceId(entity.getReferenceId())
                .referenceType(entity.getReferenceType())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public BreweryInventoryMovementEntity toEntity(BreweryInventoryMovement domain) {
        if (domain == null) return null;
        return BreweryInventoryMovementEntity.builder()
                .id(domain.getId().getValue())
                .beerStyleId(domain.getBeerStyleId().getValue())
                .type(domain.getType())
                .cansDelta(domain.getCansDelta())
                .cansBefore(domain.getCansBefore())
                .cansAfter(domain.getCansAfter())
                .referenceId(domain.getReferenceId())
                .referenceType(domain.getReferenceType())
                .notes(domain.getNotes())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}

package com.bristol.application.brewery.usecase;

import com.bristol.application.brewery.dto.BreweryBatchDto;
import com.bristol.application.brewery.dto.BreweryInventoryDto;
import com.bristol.domain.brewery.BreweryBatch;
import com.bristol.domain.brewery.BreweryInventory;
import com.bristol.domain.catalog.BeerStyle;
import com.bristol.domain.catalog.BeerStyleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BreweryApplicationMapper {

    private final BeerStyleRepository beerStyleRepository;

    public BreweryInventoryDto toDto(BreweryInventory inventory) {
        String styleName = null;
        String styleCode = null;
        BeerStyle style = beerStyleRepository.findById(inventory.getBeerStyleId()).orElse(null);
        if (style != null) {
            styleName = style.getName();
            styleCode = style.getCode();
        }
        return BreweryInventoryDto.builder()
                .id(inventory.getId().asString())
                .beerStyleId(inventory.getBeerStyleId().asString())
                .beerStyleName(styleName)
                .beerStyleCode(styleCode)
                .totalCans(inventory.getTotalCans())
                .beerStyleHasImage(style != null && style.hasImage())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }

    public BreweryBatchDto toDto(BreweryBatch batch) {
        String styleName = null;
        String styleCode = null;
        BeerStyle style = beerStyleRepository.findById(batch.getBeerStyleId()).orElse(null);
        if (style != null) {
            styleName = style.getName();
            styleCode = style.getCode();
        }
        return BreweryBatchDto.builder()
                .id(batch.getId().asString())
                .beerStyleId(batch.getBeerStyleId().asString())
                .beerStyleName(styleName)
                .beerStyleCode(styleCode)
                .cansProduced(batch.getCansProduced())
                .canCapacityMl(batch.getCanCapacityMl())
                .notes(batch.getNotes())
                .createdAt(batch.getCreatedAt())
                .build();
    }
}

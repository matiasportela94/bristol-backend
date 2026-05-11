package com.bristol.application.catalog.beerstyle.usecase;

import com.bristol.application.catalog.beerstyle.dto.BeerStyleDto;
import com.bristol.domain.catalog.BeerStyle;
import org.springframework.stereotype.Component;

/**
 * Mapper between BeerStyle domain and DTO.
 */
@Component
public class BeerStyleApplicationMapper {

    public BeerStyleDto toDto(BeerStyle beerStyle) {
        if (beerStyle == null) {
            return null;
        }

        return BeerStyleDto.builder()
                .id(beerStyle.getId().getValue().toString())
                .code(beerStyle.getCode())
                .name(beerStyle.getName())
                .description(beerStyle.getDescription())
                .category(beerStyle.getCategory())
                .active(beerStyle.isActive())
                .displayOrder(beerStyle.getDisplayOrder())
                .createdAt(beerStyle.getCreatedAt())
                .updatedAt(beerStyle.getUpdatedAt())
                .build();
    }
}

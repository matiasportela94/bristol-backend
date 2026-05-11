package com.bristol.application.product.beer.usecase;

import com.bristol.application.product.beer.dto.BeerProductDto;
import com.bristol.domain.product.BeerProduct;
import org.springframework.stereotype.Component;

/**
 * Mapper between BeerProduct domain and DTO.
 */
@Component
public class BeerProductApplicationMapper {

    public BeerProductDto toDto(BeerProduct beerProduct) {
        if (beerProduct == null) {
            return null;
        }

        return BeerProductDto.builder()
                .id(beerProduct.getId().getValue().toString())
                .name(beerProduct.getName())
                .description(beerProduct.getDescription())
                .basePrice(beerProduct.getBasePrice() != null ? beerProduct.getBasePrice().getAmount() : null)
                .beerStyleId(beerProduct.getBeerStyleId() != null ? beerProduct.getBeerStyleId().getValue().toString() : null)
                .beerCategory(beerProduct.getBeerCategory())
                .abv(beerProduct.getAbv())
                .ibu(beerProduct.getIbu() != null ? beerProduct.getIbu().intValue() : null)
                .srm(beerProduct.getSrm() != null ? beerProduct.getSrm().intValue() : null)
                .origin(beerProduct.getOrigin())
                .brewery(beerProduct.getBrewery())
                .stockQuantity(beerProduct.getStockQuantity())
                .lowStockThreshold(beerProduct.getLowStockThreshold())
                .featured(beerProduct.isFeatured())
                .createdAt(beerProduct.getCreatedAt())
                .updatedAt(beerProduct.getUpdatedAt())
                .build();
    }
}

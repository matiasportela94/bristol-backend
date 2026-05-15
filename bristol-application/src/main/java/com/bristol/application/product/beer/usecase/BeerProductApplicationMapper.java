package com.bristol.application.product.beer.usecase;

import com.bristol.application.product.beer.dto.BeerProductDto;
import com.bristol.domain.catalog.BeerStyle;
import com.bristol.domain.catalog.BeerStyleRepository;
import com.bristol.domain.product.BeerProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BeerProductApplicationMapper {

    private final BeerStyleRepository beerStyleRepository;

    public BeerProductDto toDto(BeerProduct beerProduct) {
        if (beerProduct == null) {
            return null;
        }

        BeerStyle style = beerProduct.getBeerStyleId() != null
                ? beerStyleRepository.findById(beerProduct.getBeerStyleId()).orElse(null)
                : null;

        return BeerProductDto.builder()
                .id(beerProduct.getId().getValue().toString())
                .name(beerProduct.getName())
                .description(beerProduct.getDescription())
                .basePrice(beerProduct.getBasePrice() != null ? beerProduct.getBasePrice().getAmount() : null)
                .beerStyleId(beerProduct.getBeerStyleId() != null ? beerProduct.getBeerStyleId().getValue().toString() : null)
                .beerCategory(style != null ? style.getCategory() : null)
                .abv(style != null ? style.getAbv() : null)
                .ibu(style != null ? style.getIbu() : null)
                .srm(style != null ? style.getSrm() : null)
                .origin(beerProduct.getOrigin())
                .brewery(beerProduct.getBrewery())
                .cansPerUnit(beerProduct.getCansPerUnit())
                .stockQuantity(beerProduct.getStockQuantity())
                .lowStockThreshold(beerProduct.getLowStockThreshold())
                .featured(beerProduct.isFeatured())
                .createdAt(beerProduct.getCreatedAt())
                .updatedAt(beerProduct.getUpdatedAt())
                .build();
    }
}

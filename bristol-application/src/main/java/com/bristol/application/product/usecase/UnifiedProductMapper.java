package com.bristol.application.product.usecase;

import com.bristol.application.product.dto.ProductImageDto;
import com.bristol.application.product.dto.ProductDto;
import com.bristol.application.product.dto.ProductPromotionDto;
import com.bristol.application.productvariant.dto.ProductVariantDto;
import com.bristol.application.brewery.service.BreweryStockMapService;
import com.bristol.domain.catalog.BeerStyle;
import com.bristol.domain.catalog.BeerStyleRepository;
import com.bristol.domain.product.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UnifiedProductMapper {

    private final BeerStyleRepository beerStyleRepository;
    private final BreweryStockMapService breweryStockMapService;

    public ProductDto toDto(BaseProduct product) {
        return toDto(product, List.of(), List.of(), null, List.of());
    }

    public ProductDto toDto(BaseProduct product, List<ProductImageDto> images, String primaryImageDataUrl) {
        return toDto(product, List.of(), images, primaryImageDataUrl, List.of());
    }

    public ProductDto toDto(
            BaseProduct product,
            List<ProductVariantDto> variants,
            List<ProductImageDto> images,
            String primaryImageDataUrl
    ) {
        return toDto(product, variants, images, primaryImageDataUrl, List.of());
    }

    public ProductDto toDto(
            BaseProduct product,
            List<ProductVariantDto> variants,
            List<ProductImageDto> images,
            String primaryImageDataUrl,
            List<ProductPromotionDto> promotions
    ) {
        List<ProductVariantDto> safeVariants = variants != null ? variants : List.of();

        ProductDto.ProductDtoBuilder builder = ProductDto.builder()
                .id(product.getId().getValue().toString())
                .name(product.getName())
                .description(product.getDescription())
                .category(mapProductKindToCategory(product.getProductKind()))
                .price(product.getBasePrice() != null ? product.getBasePrice().getAmount() : null)
                .stockQuantity(resolveCatalogStock(product, safeVariants))
                .minStockLevel(product.getLowStockThreshold())
                .variants(safeVariants)
                .images(images)
                .active(!product.isDeleted())
                .featured(product.isFeatured())
                .averageRating(null) // TODO: Reviews not aggregated in domain yet
                .reviewCount(null) // TODO: Reviews not aggregated in domain yet
                .promotions(promotions != null ? promotions : List.of());

        // Add type-specific fields
        if (product instanceof BeerProduct beer) {
            BeerStyle style = beer.getBeerStyleId() != null
                    ? beerStyleRepository.findById(beer.getBeerStyleId()).orElse(null)
                    : null;
            builder
                    .beerType(style != null ? mapBeerCategoryToBeerType(style.getCategory()) : null)
                    .beerStyleCode(style != null ? style.getCode() : null)
                    .beerStyleName(style != null ? style.getName() : null)
                    .abv(style != null ? style.getAbv() : null)
                    .ibu(style != null && style.getIbu() != null ? java.math.BigDecimal.valueOf(style.getIbu()) : null)
                    .srm(style != null && style.getSrm() != null ? java.math.BigDecimal.valueOf(style.getSrm()) : null)
                    .brewery(beer.getBrewery())
                    .origin(beer.getOrigin())
                    .cansPerUnit(beer.getCansPerUnit())
                    .subcategory(ProductSubcategory.CAN);
        } else if (product instanceof MerchProduct merch) {
            builder
                    .subcategory(mapMerchCategoryToSubcategory(merch.getMerchCategory()));
        } else if (product instanceof SpecialProduct special) {
            builder
                    .subcategory(ProductSubcategory.OTRO);
        }

        return builder.build();
    }

    private Integer resolveCatalogStock(BaseProduct product, List<ProductVariantDto> variants) {
        // Beer products always calculate from brewery inventory regardless of variants
        if (product instanceof BeerProduct beer && beer.getBeerStyleId() != null) {
            int cansPerUnit = beer.getCansPerUnit() != null && beer.getCansPerUnit() > 0
                    ? beer.getCansPerUnit() : 1;
            Integer totalCans = breweryStockMapService.getStockMap()
                    .get(beer.getBeerStyleId().asString());
            return totalCans != null ? totalCans / cansPerUnit : 0;
        }

        if (!variants.isEmpty()) {
            return variants.stream()
                    .map(ProductVariantDto::getStockQuantity)
                    .filter(stock -> stock != null && stock > 0)
                    .mapToInt(Integer::intValue)
                    .sum();
        }

        return product.getStockQuantity();
    }

    private ProductCategory mapProductKindToCategory(ProductKind kind) {
        return switch (kind) {
            case BEER -> ProductCategory.PRODUCTOS;
            case MERCH -> ProductCategory.MERCHANDISING;
            case SPECIAL -> ProductCategory.ESPECIALES;
        };
    }

    private BeerType mapBeerCategoryToBeerType(com.bristol.domain.catalog.BeerStyleCategory category) {
        if (category == null) return null;

        // Map BeerStyleCategory to BeerType
        return switch (category) {
            case ALE -> BeerType.APA;
            case LAGER -> BeerType.LAGER;
            case STOUT -> BeerType.STOUT;
            case WHEAT -> BeerType.WHEAT;
            case SOUR -> BeerType.SOUR;
            case SPECIALTY -> BeerType.OTRO;
        };
    }

    private ProductSubcategory mapBeerCategoryToSubcategory(com.bristol.domain.catalog.BeerStyleCategory category) {
        // Default beer subcategory
        return ProductSubcategory.CAN;
    }

    private ProductSubcategory mapMerchCategoryToSubcategory(com.bristol.domain.catalog.MerchCategory category) {
        if (category == null) return ProductSubcategory.OTRO;

        return switch (category) {
            case CLOTHING -> ProductSubcategory.REMERA;
            case GLASSWARE -> ProductSubcategory.VASO;
            case ACCESSORIES -> ProductSubcategory.GORRA;
            case OTHER -> ProductSubcategory.OTRO;
        };
    }
}

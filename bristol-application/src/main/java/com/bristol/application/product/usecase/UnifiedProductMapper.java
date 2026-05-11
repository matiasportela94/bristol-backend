package com.bristol.application.product.usecase;

import com.bristol.application.product.dto.ProductImageDto;
import com.bristol.application.product.dto.ProductDto;
import com.bristol.application.product.dto.ProductPromotionDto;
import com.bristol.application.productvariant.dto.ProductVariantDto;
import com.bristol.domain.product.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Unified mapper to convert between BaseProduct (and subclasses) and ProductDto.
 * Handles polymorphic mapping based on product type (Beer, Merch, Special).
 */
@Component
public class UnifiedProductMapper {

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
            builder
                    .beerType(mapBeerCategoryToBeerType(beer.getBeerCategory()))
                    .abv(beer.getAbv())
                    .ibu(beer.getIbu())
                    .srm(beer.getSrm())
                    .subcategory(mapBeerCategoryToSubcategory(beer.getBeerCategory()));
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

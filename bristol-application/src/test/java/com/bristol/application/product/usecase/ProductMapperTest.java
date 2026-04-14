package com.bristol.application.product.usecase;

import com.bristol.application.product.dto.ProductDto;
import com.bristol.application.productvariant.dto.ProductVariantDto;
import com.bristol.domain.product.BeerType;
import com.bristol.domain.product.Product;
import com.bristol.domain.product.ProductCategory;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductSubcategory;
import com.bristol.domain.shared.valueobject.Money;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProductMapperTest {

    private final ProductMapper mapper = new ProductMapper();

    @Test
    void toDtoShouldUseVariantStockWhenProductHasVariants() {
        Product product = Product.builder()
                .id(ProductId.generate())
                .name("Six Pack Bristol Pale Ale")
                .description("Pack de 6 latas")
                .category(ProductCategory.PRODUCTOS)
                .subcategory(ProductSubcategory.SIX_PACK)
                .beerType(BeerType.PALE_ALE)
                .basePrice(Money.of(7800))
                .stockQuantity(0)
                .lowStockThreshold(5)
                .createdAt(Instant.parse("2026-04-14T13:00:00Z"))
                .updatedAt(Instant.parse("2026-04-14T13:00:00Z"))
                .build();

        ProductVariantDto firstVariant = ProductVariantDto.builder()
                .id("variant-1")
                .size("433ml")
                .sizeMl(433)
                .stockQuantity(1)
                .inStock(true)
                .build();
        ProductVariantDto secondVariant = ProductVariantDto.builder()
                .id("variant-2")
                .size("473ml")
                .sizeMl(473)
                .stockQuantity(2)
                .inStock(true)
                .build();

        ProductDto dto = mapper.toDto(product, List.of(firstVariant, secondVariant), List.of(), null, List.of());

        assertThat(dto.getStockQuantity()).isEqualTo(3);
    }

    @Test
    void toDtoShouldKeepBaseStockWhenProductHasNoVariants() {
        Product product = Product.builder()
                .id(ProductId.generate())
                .name("Lata Bristol IPA")
                .description("Lata individual")
                .category(ProductCategory.PRODUCTOS)
                .subcategory(ProductSubcategory.CAN)
                .beerType(BeerType.IPA)
                .basePrice(Money.of(3200))
                .stockQuantity(12)
                .lowStockThreshold(5)
                .createdAt(Instant.parse("2026-04-14T13:00:00Z"))
                .updatedAt(Instant.parse("2026-04-14T13:00:00Z"))
                .build();

        ProductDto dto = mapper.toDto(product, List.of(), List.of(), null, List.of());

        assertThat(dto.getStockQuantity()).isEqualTo(12);
    }
}

package com.bristol.infrastructure.persistence.mapper;

import com.bristol.domain.product.BeerType;
import com.bristol.domain.product.Product;
import com.bristol.domain.product.ProductCategory;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductSubcategory;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.infrastructure.persistence.entity.ProductEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProductMapperTest {

    private final ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

    @Test
    void toEntityShouldPreserveCurrentDomainEnums() {
        Product product = Product.create(
                "Golden",
                "Golden beer",
                ProductCategory.PRODUCTOS,
                ProductSubcategory.SIX_PACK,
                BeerType.GOLDEN,
                Money.of(150),
                10,
                2,
                Instant.parse("2026-03-31T12:00:00Z")
        );

        ProductEntity entity = mapper.toEntity(product);

        assertThat(entity.getCategory()).isEqualTo(ProductEntity.ProductCategoryEnum.PRODUCTOS);
        assertThat(entity.getSubcategory()).isEqualTo(ProductEntity.ProductSubcategoryEnum.SIX_PACK);
        assertThat(entity.getBeerType()).isEqualTo(ProductEntity.BeerTypeEnum.GOLDEN);
        assertThat(entity.getIsFeatured()).isFalse();
    }

    @Test
    void toDomainShouldPreserveFeaturedFlagAndEnums() {
        ProductEntity entity = ProductEntity.builder()
                .id(UUID.randomUUID())
                .name("Gorra Bristol")
                .description("Merch")
                .category(ProductEntity.ProductCategoryEnum.MERCHANDISING)
                .subcategory(ProductEntity.ProductSubcategoryEnum.GORRA)
                .basePrice(BigDecimal.valueOf(200))
                .stockQuantity(5)
                .lowStockThreshold(1)
                .isActive(true)
                .isFeatured(true)
                .totalReviews(0L)
                .discountPercentage(BigDecimal.ZERO)
                .createdAt(Instant.parse("2026-03-31T12:00:00Z"))
                .updatedAt(Instant.parse("2026-03-31T12:00:00Z"))
                .build();

        Product product = mapper.toDomain(entity);

        assertThat(product.getCategory()).isEqualTo(ProductCategory.MERCHANDISING);
        assertThat(product.getSubcategory()).isEqualTo(ProductSubcategory.GORRA);
        assertThat(product.isFeatured()).isTrue();
    }

    @Test
    void toEntityShouldMapPaleAleBeerType() {
        Product product = Product.create(
                "Pale Ale",
                "Pale ale beer",
                ProductCategory.PRODUCTOS,
                ProductSubcategory.CAN,
                BeerType.PALE_ALE,
                Money.of(200),
                8,
                2,
                Instant.parse("2026-03-31T12:00:00Z")
        );

        ProductEntity entity = mapper.toEntity(product);

        assertThat(entity.getBeerType()).isEqualTo(ProductEntity.BeerTypeEnum.PALE_ALE);
    }
}

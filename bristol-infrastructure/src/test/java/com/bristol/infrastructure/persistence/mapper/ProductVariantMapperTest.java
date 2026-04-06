package com.bristol.infrastructure.persistence.mapper;

import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductVariant;
import com.bristol.domain.product.ProductVariantId;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.infrastructure.persistence.entity.ProductVariantEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProductVariantMapperTest {

    private final ProductVariantMapper mapper = Mappers.getMapper(ProductVariantMapper.class);

    @Test
    void toDomainShouldUseVariantNameWhenSizeMlIsMissing() {
        ProductVariantEntity entity = ProductVariantEntity.builder()
                .id(UUID.randomUUID())
                .productId(UUID.randomUUID())
                .sku("TN-REMERA-XL")
                .variantName("XL")
                .sizeMl(null)
                .additionalPrice(BigDecimal.ZERO)
                .stockQuantity(6)
                .isActive(true)
                .createdAt(Instant.parse("2026-03-31T12:00:00Z"))
                .updatedAt(Instant.parse("2026-03-31T12:00:00Z"))
                .build();

        ProductVariant variant = mapper.toDomain(entity);

        assertThat(variant.getSize()).isEqualTo("XL");
        assertThat(variant.getSku()).isEqualTo("TN-REMERA-XL");
    }

    @Test
    void toEntityShouldStoreClothingSizesInVariantName() {
        ProductVariant variant = ProductVariant.create(
                new ProductId(UUID.randomUUID()),
                "TN-BUZO-XXL",
                "XXL",
                null,
                Money.zero(),
                1,
                null,
                Instant.parse("2026-03-31T12:00:00Z")
        ).toBuilder()
                .id(new ProductVariantId(UUID.randomUUID()))
                .build();

        ProductVariantEntity entity = mapper.toEntity(variant);

        assertThat(entity.getVariantName()).isEqualTo("XXL");
        assertThat(entity.getSizeMl()).isNull();
    }
}
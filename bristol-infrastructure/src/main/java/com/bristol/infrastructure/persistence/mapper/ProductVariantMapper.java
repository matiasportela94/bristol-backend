package com.bristol.infrastructure.persistence.mapper;

import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductVariant;
import com.bristol.domain.product.ProductVariantId;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.infrastructure.persistence.entity.ProductVariantEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * MapStruct mapper for ProductVariant domain object and ProductVariantEntity.
 */
@Mapper(componentModel = "spring")
public interface ProductVariantMapper {

    @Mapping(target = "id", expression = "java(toProductVariantId(entity.getId()))")
    @Mapping(target = "productId", expression = "java(toProductId(entity.getProductId()))")
    @Mapping(target = "sku", source = "sku")
    @Mapping(target = "size", expression = "java(resolveSize(entity))")
    @Mapping(target = "sizeMl", source = "sizeMl")
    @Mapping(target = "color", source = "color")
    @Mapping(target = "additionalPrice", expression = "java(toMoney(entity.getAdditionalPrice()))")
    @Mapping(target = "stockQuantity", source = "stockQuantity")
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    ProductVariant toDomain(ProductVariantEntity entity);

    @Mapping(target = "id", expression = "java(toUUID(domain.getId()))")
    @Mapping(target = "productId", expression = "java(toUUID(domain.getProductId()))")
    @Mapping(target = "sku", source = "sku")
    @Mapping(target = "variantName", expression = "java(domain.getSize() != null ? domain.getSize() : \"Default\")")
    @Mapping(target = "sizeMl", expression = "java(domain.getSizeMl() != null ? domain.getSizeMl() : parseSizeMl(domain.getSize()))")
    @Mapping(target = "color", source = "color")
    @Mapping(target = "additionalPrice", expression = "java(toBigDecimal(domain.getAdditionalPrice()))")
    @Mapping(target = "stockQuantity", source = "stockQuantity")
    @Mapping(target = "isActive", constant = "true")
    ProductVariantEntity toEntity(ProductVariant domain);

    default String resolveSize(ProductVariantEntity entity) {
        if (entity == null) {
            return null;
        }
        if (entity.getSizeMl() != null) {
            return entity.getSizeMl() + "ml";
        }
        return entity.getVariantName();
    }

    default ProductVariantId toProductVariantId(UUID uuid) {
        return uuid != null ? new ProductVariantId(uuid) : null;
    }

    default ProductId toProductId(UUID uuid) {
        return uuid != null ? new ProductId(uuid) : null;
    }

    default UUID toUUID(ProductVariantId id) {
        return id != null ? id.getValue() : null;
    }

    default UUID toUUID(ProductId id) {
        return id != null ? id.getValue() : null;
    }

    default Money toMoney(BigDecimal amount) {
        return amount != null ? Money.of(amount) : Money.zero();
    }

    default BigDecimal toBigDecimal(Money money) {
        return money != null ? money.getAmount() : BigDecimal.ZERO;
    }

    default Integer parseSizeMl(String size) {
        if (size == null || size.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(size.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

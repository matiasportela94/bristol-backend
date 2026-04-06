package com.bristol.infrastructure.persistence.mapper;

import com.bristol.domain.product.*;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.infrastructure.persistence.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * MapStruct mapper for Product domain object and ProductEntity.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", expression = "java(toProductId(entity.getId()))")
    @Mapping(target = "category", expression = "java(toDomainCategory(entity.getCategory()))")
    @Mapping(target = "subcategory", expression = "java(toDomainSubcategory(entity.getSubcategory()))")
    @Mapping(target = "beerType", expression = "java(toDomainBeerType(entity.getBeerType()))")
    @Mapping(target = "basePrice", expression = "java(toMoney(entity.getBasePrice()))")
    @Mapping(target = "featured", expression = "java(Boolean.TRUE.equals(entity.getIsFeatured()))")
    @Mapping(target = "deletedAt", source = "deletedAt")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    Product toDomain(ProductEntity entity);

    @Mapping(target = "id", expression = "java(toUUID(domain.getId()))")
    @Mapping(target = "category", expression = "java(toEntityCategory(domain.getCategory()))")
    @Mapping(target = "subcategory", expression = "java(toEntitySubcategory(domain.getSubcategory()))")
    @Mapping(target = "beerType", expression = "java(toEntityBeerType(domain.getBeerType()))")
    @Mapping(target = "basePrice", expression = "java(toBigDecimal(domain.getBasePrice()))")
    @Mapping(target = "brewingMethod", ignore = true)
    @Mapping(target = "abv", ignore = true)
    @Mapping(target = "ibu", ignore = true)
    @Mapping(target = "srm", ignore = true)
    @Mapping(target = "flavor", ignore = true)
    @Mapping(target = "bitterness", ignore = true)
    @Mapping(target = "discountPercentage", constant = "0")
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "isFeatured", expression = "java(domain.isFeatured())")
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "totalReviews", constant = "0L")
    ProductEntity toEntity(Product domain);

    // ID conversions
    default ProductId toProductId(UUID uuid) {
        return uuid != null ? new ProductId(uuid) : null;
    }

    default UUID toUUID(ProductId id) {
        return id != null ? id.getValue() : null;
    }

    // Money conversions
    default Money toMoney(BigDecimal amount) {
        return amount != null ? Money.of(amount) : null;
    }

    default BigDecimal toBigDecimal(Money money) {
        return money != null ? money.getAmount() : null;
    }

    // Enum conversions
    default ProductCategory toDomainCategory(ProductEntity.ProductCategoryEnum category) {
        return category != null ? ProductCategory.valueOf(category.name()) : null;
    }

    default ProductEntity.ProductCategoryEnum toEntityCategory(ProductCategory category) {
        return category != null ? ProductEntity.ProductCategoryEnum.valueOf(category.name()) : null;
    }

    default ProductSubcategory toDomainSubcategory(ProductEntity.ProductSubcategoryEnum subcategory) {
        return subcategory != null ? ProductSubcategory.valueOf(subcategory.name()) : null;
    }

    default ProductEntity.ProductSubcategoryEnum toEntitySubcategory(ProductSubcategory subcategory) {
        return subcategory != null ? ProductEntity.ProductSubcategoryEnum.valueOf(subcategory.name()) : null;
    }

    default BeerType toDomainBeerType(ProductEntity.BeerTypeEnum beerType) {
        return beerType != null ? BeerType.valueOf(beerType.name()) : null;
    }

    default ProductEntity.BeerTypeEnum toEntityBeerType(BeerType beerType) {
        return beerType != null ? ProductEntity.BeerTypeEnum.valueOf(beerType.name()) : null;
    }

}

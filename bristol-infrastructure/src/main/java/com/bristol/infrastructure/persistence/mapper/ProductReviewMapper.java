package com.bristol.infrastructure.persistence.mapper;

import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductReview;
import com.bristol.domain.product.ProductReviewId;
import com.bristol.domain.shared.valueobject.Rating;
import com.bristol.domain.user.UserId;
import com.bristol.infrastructure.persistence.entity.ProductReviewEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

/**
 * MapStruct mapper for ProductReview domain object and ProductReviewEntity.
 */
@Mapper(componentModel = "spring")
public interface ProductReviewMapper {

    @Mapping(target = "id", expression = "java(toProductReviewId(entity.getId()))")
    @Mapping(target = "productId", expression = "java(toProductId(entity.getProductId()))")
    @Mapping(target = "userId", expression = "java(toUserId(entity.getUserId()))")
    @Mapping(target = "rating", expression = "java(toRating(entity.getRating()))")
    ProductReview toDomain(ProductReviewEntity entity);

    @Mapping(target = "id", expression = "java(toUUID(domain.getId()))")
    @Mapping(target = "productId", expression = "java(toUUID(domain.getProductId()))")
    @Mapping(target = "userId", expression = "java(toUUID(domain.getUserId()))")
    @Mapping(target = "rating", expression = "java(toInteger(domain.getRating()))")
    ProductReviewEntity toEntity(ProductReview domain);

    default ProductReviewId toProductReviewId(UUID uuid) {
        return uuid != null ? new ProductReviewId(uuid) : null;
    }

    default ProductId toProductId(UUID uuid) {
        return uuid != null ? new ProductId(uuid) : null;
    }

    default UserId toUserId(UUID uuid) {
        return uuid != null ? new UserId(uuid) : null;
    }

    default UUID toUUID(ProductReviewId id) {
        return id != null ? id.getValue() : null;
    }

    default UUID toUUID(ProductId id) {
        return id != null ? id.getValue() : null;
    }

    default UUID toUUID(UserId id) {
        return id != null ? id.getValue() : null;
    }

    default Rating toRating(Integer value) {
        return value != null ? Rating.of(value) : null;
    }

    default Integer toInteger(Rating rating) {
        return rating != null ? rating.getValue() : null;
    }
}

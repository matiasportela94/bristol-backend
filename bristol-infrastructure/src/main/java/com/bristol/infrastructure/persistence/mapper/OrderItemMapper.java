package com.bristol.infrastructure.persistence.mapper;

import com.bristol.domain.coupon.CouponId;
import com.bristol.domain.order.OrderId;
import com.bristol.domain.order.OrderItem;
import com.bristol.domain.order.OrderItemId;
import com.bristol.domain.order.ProductType;
import com.bristol.domain.product.BeerType;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductVariantId;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.infrastructure.persistence.entity.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * MapStruct mapper for OrderItem domain object and OrderItemEntity.
 */
@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "id", expression = "java(toOrderItemId(entity.getId()))")
    @Mapping(target = "orderId", expression = "java(toOrderId(entity.getOrderId()))")
    @Mapping(target = "productId", expression = "java(toProductId(entity.getProductId()))")
    @Mapping(target = "productVariantId", expression = "java(toProductVariantId(entity.getProductVariantId()))")
    @Mapping(target = "productType", expression = "java(toDomainProductType(entity.getProductType()))")
    @Mapping(target = "beerType", expression = "java(toDomainBeerType(entity.getBeerType()))")
    @Mapping(target = "pricePerUnit", expression = "java(toMoney(entity.getPricePerUnit()))")
    @Mapping(target = "itemDiscountCouponId", expression = "java(toCouponId(entity.getItemDiscountCouponId()))")
    @Mapping(target = "itemDiscountAmount", expression = "java(toMoney(entity.getItemDiscountAmount()))")
    @Mapping(target = "subtotal", expression = "java(toMoney(entity.getSubtotal()))")
    OrderItem toDomain(OrderItemEntity entity);

    @Mapping(target = "id", expression = "java(toUUID(domain.getId()))")
    @Mapping(target = "orderId", expression = "java(toUUID(domain.getOrderId()))")
    @Mapping(target = "productId", expression = "java(toUUID(domain.getProductId()))")
    @Mapping(target = "productVariantId", expression = "java(toUUID(domain.getProductVariantId()))")
    @Mapping(target = "productType", expression = "java(toEntityProductType(domain.getProductType()))")
    @Mapping(target = "beerType", expression = "java(toEntityBeerType(domain.getBeerType()))")
    @Mapping(target = "pricePerUnit", expression = "java(toBigDecimal(domain.getPricePerUnit()))")
    @Mapping(target = "itemDiscountCouponId", expression = "java(toUUID(domain.getItemDiscountCouponId()))")
    @Mapping(target = "itemDiscountAmount", expression = "java(toBigDecimal(domain.getItemDiscountAmount()))")
    @Mapping(target = "subtotal", expression = "java(toBigDecimal(domain.getSubtotal()))")
    OrderItemEntity toEntity(OrderItem domain);

    // ID conversions
    default OrderItemId toOrderItemId(UUID uuid) {
        return uuid != null ? new OrderItemId(uuid) : null;
    }

    default OrderId toOrderId(UUID uuid) {
        return uuid != null ? new OrderId(uuid) : null;
    }

    default ProductId toProductId(UUID uuid) {
        return uuid != null ? new ProductId(uuid) : null;
    }

    default ProductVariantId toProductVariantId(UUID uuid) {
        return uuid != null ? new ProductVariantId(uuid) : null;
    }

    default CouponId toCouponId(UUID uuid) {
        return uuid != null ? new CouponId(uuid) : null;
    }

    default UUID toUUID(OrderItemId id) {
        return id != null ? id.getValue() : null;
    }

    default UUID toUUID(OrderId id) {
        return id != null ? id.getValue() : null;
    }

    default UUID toUUID(ProductId id) {
        return id != null ? id.getValue() : null;
    }

    default UUID toUUID(ProductVariantId id) {
        return id != null ? id.getValue() : null;
    }

    default UUID toUUID(CouponId id) {
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
    default ProductType toDomainProductType(OrderItemEntity.ProductTypeEnum productType) {
        return productType != null ? ProductType.valueOf(productType.name()) : null;
    }

    default OrderItemEntity.ProductTypeEnum toEntityProductType(ProductType productType) {
        return productType != null ? OrderItemEntity.ProductTypeEnum.valueOf(productType.name()) : null;
    }

    default BeerType toDomainBeerType(OrderItemEntity.BeerTypeEnum beerType) {
        return beerType != null ? BeerType.valueOf(beerType.name()) : null;
    }

    default OrderItemEntity.BeerTypeEnum toEntityBeerType(BeerType beerType) {
        return beerType != null ? OrderItemEntity.BeerTypeEnum.valueOf(beerType.name()) : null;
    }
}

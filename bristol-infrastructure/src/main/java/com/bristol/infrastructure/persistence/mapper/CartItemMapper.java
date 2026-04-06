package com.bristol.infrastructure.persistence.mapper;

import com.bristol.domain.cart.CartItem;
import com.bristol.domain.cart.CartItemId;
import com.bristol.domain.cart.ShoppingCartId;
import com.bristol.domain.order.ProductType;
import com.bristol.domain.product.BeerType;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductVariantId;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.infrastructure.persistence.entity.CartItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    @Mapping(target = "id", expression = "java(toCartItemId(entity.getId()))")
    @Mapping(target = "cartId", expression = "java(toShoppingCartId(entity.getCartId()))")
    @Mapping(target = "productId", expression = "java(toProductId(entity.getProductId()))")
    @Mapping(target = "productVariantId", expression = "java(toProductVariantId(entity.getProductVariantId()))")
    @Mapping(target = "productType", expression = "java(toDomainProductType(entity.getProductType()))")
    @Mapping(target = "beerType", expression = "java(toDomainBeerType(entity.getBeerType()))")
    @Mapping(target = "unitPrice", expression = "java(toMoney(entity.getUnitPrice()))")
    @Mapping(target = "subtotal", expression = "java(toMoney(entity.getSubtotal()))")
    CartItem toDomain(CartItemEntity entity);

    @Mapping(target = "id", expression = "java(toUUID(domain.getId()))")
    @Mapping(target = "cartId", expression = "java(toUUID(domain.getCartId()))")
    @Mapping(target = "productId", expression = "java(toUUID(domain.getProductId()))")
    @Mapping(target = "productVariantId", expression = "java(toUUID(domain.getProductVariantId()))")
    @Mapping(target = "productType", expression = "java(toEntityProductType(domain.getProductType()))")
    @Mapping(target = "beerType", expression = "java(toEntityBeerType(domain.getBeerType()))")
    @Mapping(target = "unitPrice", expression = "java(toBigDecimal(domain.getUnitPrice()))")
    @Mapping(target = "subtotal", expression = "java(toBigDecimal(domain.getSubtotal()))")
    CartItemEntity toEntity(CartItem domain);

    default CartItemId toCartItemId(UUID uuid) {
        return uuid != null ? new CartItemId(uuid) : null;
    }

    default ShoppingCartId toShoppingCartId(UUID uuid) {
        return uuid != null ? new ShoppingCartId(uuid) : null;
    }

    default ProductId toProductId(UUID uuid) {
        return uuid != null ? new ProductId(uuid) : null;
    }

    default ProductVariantId toProductVariantId(UUID uuid) {
        return uuid != null ? new ProductVariantId(uuid) : null;
    }

    default UUID toUUID(CartItemId id) {
        return id != null ? id.getValue() : null;
    }

    default UUID toUUID(ShoppingCartId id) {
        return id != null ? id.getValue() : null;
    }

    default UUID toUUID(ProductId id) {
        return id != null ? id.getValue() : null;
    }

    default UUID toUUID(ProductVariantId id) {
        return id != null ? id.getValue() : null;
    }

    default Money toMoney(BigDecimal amount) {
        return amount != null ? Money.of(amount) : null;
    }

    default BigDecimal toBigDecimal(Money money) {
        return money != null ? money.getAmount() : null;
    }

    default ProductType toDomainProductType(CartItemEntity.ProductTypeEnum productType) {
        return productType != null ? ProductType.valueOf(productType.name()) : null;
    }

    default CartItemEntity.ProductTypeEnum toEntityProductType(ProductType productType) {
        return productType != null ? CartItemEntity.ProductTypeEnum.valueOf(productType.name()) : null;
    }

    default BeerType toDomainBeerType(CartItemEntity.BeerTypeEnum beerType) {
        return beerType != null ? BeerType.valueOf(beerType.name()) : null;
    }

    default CartItemEntity.BeerTypeEnum toEntityBeerType(BeerType beerType) {
        return beerType != null ? CartItemEntity.BeerTypeEnum.valueOf(beerType.name()) : null;
    }
}

package com.bristol.infrastructure.persistence.mapper;

import com.bristol.domain.cart.ShoppingCart;
import com.bristol.domain.cart.ShoppingCartId;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.UserId;
import com.bristol.infrastructure.persistence.entity.ShoppingCartEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ShoppingCartMapper {

    @Mapping(target = "id", expression = "java(toShoppingCartId(entity.getId()))")
    @Mapping(target = "userId", expression = "java(toUserId(entity.getUserId()))")
    @Mapping(target = "subtotal", expression = "java(toMoney(entity.getSubtotal()))")
    @Mapping(target = "items", ignore = true)
    ShoppingCart toDomain(ShoppingCartEntity entity);

    @Mapping(target = "id", expression = "java(toUUID(domain.getId()))")
    @Mapping(target = "userId", expression = "java(toUUID(domain.getUserId()))")
    @Mapping(target = "subtotal", expression = "java(toBigDecimal(domain.getSubtotal()))")
    ShoppingCartEntity toEntity(ShoppingCart domain);

    default ShoppingCartId toShoppingCartId(UUID uuid) {
        return uuid != null ? new ShoppingCartId(uuid) : null;
    }

    default UserId toUserId(UUID uuid) {
        return uuid != null ? new UserId(uuid) : null;
    }

    default UUID toUUID(ShoppingCartId id) {
        return id != null ? id.getValue() : null;
    }

    default UUID toUUID(UserId id) {
        return id != null ? id.getValue() : null;
    }

    default Money toMoney(BigDecimal amount) {
        return amount != null ? Money.of(amount) : null;
    }

    default BigDecimal toBigDecimal(Money money) {
        return money != null ? money.getAmount() : null;
    }
}

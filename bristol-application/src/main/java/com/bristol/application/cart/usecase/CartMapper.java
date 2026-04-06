package com.bristol.application.cart.usecase;

import com.bristol.application.cart.dto.CartDto;
import com.bristol.application.cart.dto.CartItemDto;
import com.bristol.domain.cart.CartItem;
import com.bristol.domain.cart.ShoppingCart;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CartMapper {

    public CartDto toDto(ShoppingCart cart) {
        return CartDto.builder()
                .id(cart.getId().getValue().toString())
                .userId(cart.getUserId().getValue().toString())
                .items(cart.getItems().stream()
                        .map(this::toItemDto)
                        .collect(Collectors.toList()))
                .subtotal(cart.getSubtotal().getAmount())
                .totalItems(cart.getTotalItems())
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }

    private CartItemDto toItemDto(CartItem item) {
        return CartItemDto.builder()
                .id(item.getId().getValue().toString())
                .productId(item.getProductId().getValue().toString())
                .productVariantId(item.getProductVariantId() != null ? item.getProductVariantId().getValue().toString() : null)
                .productName(item.getProductName())
                .productType(item.getProductType())
                .beerType(item.getBeerType())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice().getAmount())
                .subtotal(item.getSubtotal().getAmount())
                .build();
    }
}

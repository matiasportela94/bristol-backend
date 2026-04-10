package com.bristol.application.cart.usecase;

import com.bristol.application.cart.dto.CartDto;
import com.bristol.application.cart.dto.CartItemDto;
import com.bristol.domain.cart.CartItem;
import com.bristol.domain.cart.ShoppingCart;
import com.bristol.domain.order.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CartMapper {

    private final CartPricingPreviewService cartPricingPreviewService;

    public CartDto toDto(ShoppingCart cart) {
        return toDto(cart, null);
    }

    public CartDto toDto(ShoppingCart cart, String couponCode) {
        CartPricingPreviewService.CartPricingPreview preview = cartPricingPreviewService.preview(cart, couponCode);

        return CartDto.builder()
                .id(cart.getId().getValue().toString())
                .userId(cart.getUserId().getValue().toString())
                .items(cart.getItems().stream()
                        .map(item -> toItemDto(item, preview))
                        .collect(Collectors.toList()))
                .originalSubtotal(preview.originalSubtotal())
                .productDiscountAmount(preview.productDiscountAmount())
                .subtotal(preview.subtotal())
                .orderDiscountAmount(preview.orderDiscountAmount())
                .total(preview.total())
                .totalItems(cart.getTotalItems())
                .appliedOrderPromotion(preview.appliedOrderPromotion())
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }

    private CartItemDto toItemDto(CartItem item, CartPricingPreviewService.CartPricingPreview preview) {
        OrderItem repricedItem = preview.findItem(
                item.getProductId().getValue().toString(),
                item.getProductVariantId() != null ? item.getProductVariantId().getValue().toString() : null
        ).orElse(null);

        return CartItemDto.builder()
                .id(item.getId().getValue().toString())
                .productId(item.getProductId().getValue().toString())
                .productVariantId(item.getProductVariantId() != null ? item.getProductVariantId().getValue().toString() : null)
                .productName(item.getProductName())
                .productType(item.getProductType())
                .beerType(item.getBeerType())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice().getAmount())
                .originalSubtotal(repricedItem != null ? repricedItem.getOriginalSubtotal().getAmount() : item.getSubtotal().getAmount())
                .itemDiscountAmount(repricedItem != null ? repricedItem.getItemDiscountAmount().getAmount() : java.math.BigDecimal.ZERO)
                .subtotal(repricedItem != null ? repricedItem.getSubtotal().getAmount() : item.getSubtotal().getAmount())
                .appliedPromotion(repricedItem != null && !repricedItem.getItemDiscountAmount().isZero()
                        ? preview.findAppliedProductPromotion(
                                repricedItem.getItemDiscountCouponId() != null
                                        ? repricedItem.getItemDiscountCouponId().getValue().toString()
                                        : null
                        ).orElse(null)
                        : null)
                .build();
    }
}

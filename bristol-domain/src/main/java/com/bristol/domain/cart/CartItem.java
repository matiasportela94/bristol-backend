package com.bristol.domain.cart;

import com.bristol.domain.order.ProductType;
import com.bristol.domain.product.BeerType;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductVariantId;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.valueobject.Money;
import lombok.Builder;
import lombok.Getter;

/**
 * Cart line item.
 */
@Getter
@Builder(toBuilder = true)
public class CartItem {

    private final CartItemId id;
    private final ShoppingCartId cartId;
    private final ProductId productId;
    private final ProductVariantId productVariantId;
    private final String productName;
    private final ProductType productType;
    private final BeerType beerType;
    private final Integer quantity;
    private final Money unitPrice;
    private final Money subtotal;

    public static CartItem create(
            ShoppingCartId cartId,
            ProductId productId,
            ProductVariantId productVariantId,
            String productName,
            ProductType productType,
            BeerType beerType,
            Integer quantity,
            Money unitPrice
    ) {
        validate(cartId, productId, productName, productType, quantity, unitPrice);

        return CartItem.builder()
                .id(CartItemId.generate())
                .cartId(cartId)
                .productId(productId)
                .productVariantId(productVariantId)
                .productName(productName)
                .productType(productType)
                .beerType(beerType)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .subtotal(unitPrice.multiply(quantity))
                .build();
    }

    public CartItem updateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new ValidationException("Quantity must be greater than zero");
        }

        return this.toBuilder()
                .quantity(quantity)
                .subtotal(unitPrice.multiply(quantity))
                .build();
    }

    public CartItem updateSnapshot(
            String productName,
            ProductType productType,
            BeerType beerType,
            Integer quantity,
            Money unitPrice
    ) {
        validate(cartId, productId, productName, productType, quantity, unitPrice);

        return this.toBuilder()
                .productName(productName)
                .productType(productType)
                .beerType(beerType)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .subtotal(unitPrice.multiply(quantity))
                .build();
    }

    public boolean isSameProduct(ProductId productId, ProductVariantId productVariantId) {
        if (!this.productId.equals(productId)) {
            return false;
        }

        if (this.productVariantId == null && productVariantId == null) {
            return true;
        }

        return this.productVariantId != null && this.productVariantId.equals(productVariantId);
    }

    private static void validate(
            ShoppingCartId cartId,
            ProductId productId,
            String productName,
            ProductType productType,
            Integer quantity,
            Money unitPrice
    ) {
        if (cartId == null) {
            throw new ValidationException("Cart ID is required");
        }
        if (productId == null) {
            throw new ValidationException("Product ID is required");
        }
        if (productName == null || productName.trim().isEmpty()) {
            throw new ValidationException("Product name is required");
        }
        if (productType == null) {
            throw new ValidationException("Product type is required");
        }
        if (quantity == null || quantity <= 0) {
            throw new ValidationException("Quantity must be greater than zero");
        }
        if (unitPrice == null || unitPrice.isNegative()) {
            throw new ValidationException("Unit price must be zero or positive");
        }
    }
}

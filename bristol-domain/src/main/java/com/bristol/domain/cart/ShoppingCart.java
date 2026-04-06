package com.bristol.domain.cart;

import com.bristol.domain.order.ProductType;
import com.bristol.domain.product.BeerType;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductVariantId;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.UserId;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Shopping cart aggregate root.
 */
@Getter
@Builder(toBuilder = true)
public class ShoppingCart {

    private final ShoppingCartId id;
    private final UserId userId;

    @Builder.Default
    private final List<CartItem> items = new ArrayList<>();

    private final Money subtotal;
    private final Integer totalItems;
    private final Instant createdAt;
    private final Instant updatedAt;

    public static ShoppingCart create(UserId userId, Instant now) {
        if (userId == null) {
            throw new ValidationException("User ID is required");
        }
        if (now == null) {
            throw new ValidationException("Current timestamp is required");
        }

        return ShoppingCart.builder()
                .id(ShoppingCartId.generate())
                .userId(userId)
                .items(new ArrayList<>())
                .subtotal(Money.zero())
                .totalItems(0)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public ShoppingCart addItem(
            ProductId productId,
            ProductVariantId productVariantId,
            String productName,
            ProductType productType,
            BeerType beerType,
            Integer quantity,
            Money unitPrice,
            Instant now
    ) {
        validateTimestamp(now);

        List<CartItem> updatedItems = new ArrayList<>(items);
        int existingIndex = findItemIndex(productId, productVariantId);

        if (existingIndex >= 0) {
            CartItem existingItem = updatedItems.get(existingIndex);
            updatedItems.set(existingIndex, existingItem.updateSnapshot(
                    productName,
                    productType,
                    beerType,
                    existingItem.getQuantity() + quantity,
                    unitPrice
            ));
        } else {
            updatedItems.add(CartItem.create(
                    id,
                    productId,
                    productVariantId,
                    productName,
                    productType,
                    beerType,
                    quantity,
                    unitPrice
            ));
        }

        return rebuild(updatedItems, now);
    }

    public ShoppingCart updateItemQuantity(CartItemId itemId, Integer quantity, Instant now) {
        validateTimestamp(now);

        List<CartItem> updatedItems = new ArrayList<>(items);
        int itemIndex = findItemIndex(itemId);
        if (itemIndex < 0) {
            throw new ValidationException("Cart item not found: " + itemId);
        }

        updatedItems.set(itemIndex, updatedItems.get(itemIndex).updateQuantity(quantity));
        return rebuild(updatedItems, now);
    }

    public ShoppingCart replaceItem(CartItem item, Instant now) {
        validateTimestamp(now);

        List<CartItem> updatedItems = new ArrayList<>(items);
        int itemIndex = findItemIndex(item.getId());
        if (itemIndex < 0) {
            throw new ValidationException("Cart item not found: " + item.getId());
        }

        updatedItems.set(itemIndex, item);
        return rebuild(updatedItems, now);
    }

    public ShoppingCart removeItem(CartItemId itemId, Instant now) {
        validateTimestamp(now);
        return rebuild(items.stream()
                .filter(item -> !item.getId().equals(itemId))
                .collect(Collectors.toCollection(ArrayList::new)), now);
    }

    public ShoppingCart clear(Instant now) {
        validateTimestamp(now);
        return rebuild(new ArrayList<>(), now);
    }

    public ShoppingCart replaceItems(List<CartItem> items, Instant now) {
        validateTimestamp(now);
        return rebuild(new ArrayList<>(items), now);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    private ShoppingCart rebuild(List<CartItem> updatedItems, Instant now) {
        Money newSubtotal = updatedItems.stream()
                .map(CartItem::getSubtotal)
                .reduce(Money.zero(), Money::add);
        int newTotalItems = updatedItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        return this.toBuilder()
                .items(updatedItems)
                .subtotal(newSubtotal)
                .totalItems(newTotalItems)
                .updatedAt(now)
                .build();
    }

    private int findItemIndex(ProductId productId, ProductVariantId productVariantId) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isSameProduct(productId, productVariantId)) {
                return i;
            }
        }
        return -1;
    }

    private int findItemIndex(CartItemId itemId) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equals(itemId)) {
                return i;
            }
        }
        return -1;
    }

    private void validateTimestamp(Instant now) {
        if (now == null) {
            throw new ValidationException("Current timestamp is required");
        }
    }
}

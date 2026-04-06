package com.bristol.application.cart.usecase;

import com.bristol.domain.cart.ShoppingCart;
import com.bristol.domain.cart.ShoppingCartRepository;
import com.bristol.domain.order.ProductType;
import com.bristol.domain.product.*;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.User;
import com.bristol.domain.user.UserRepository;

import java.util.Optional;

abstract class CartCommandSupport {

    protected User resolveUserByEmail(String email, UserRepository userRepository) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ValidationException("Authenticated user not found: " + email));
    }

    protected ShoppingCart getOrCreateCart(User user, ShoppingCartRepository shoppingCartRepository) {
        return shoppingCartRepository.findByUserId(user.getId())
                .orElseGet(() -> ShoppingCart.create(user.getId(), java.time.Instant.now()));
    }

    protected Product productOrThrow(String productId, ProductRepository productRepository) {
        return productRepository.findById(new ProductId(productId))
                .orElseThrow(() -> new ValidationException("Product not found: " + productId));
    }

    protected Optional<ProductVariant> resolveVariant(
            String productVariantId,
            Product product,
            ProductVariantRepository productVariantRepository
    ) {
        if (productVariantId == null || productVariantId.isBlank()) {
            return Optional.empty();
        }

        ProductVariant variant = productVariantRepository.findById(new ProductVariantId(productVariantId))
                .orElseThrow(() -> new ValidationException("Product variant not found: " + productVariantId));

        if (!variant.getProductId().equals(product.getId())) {
            throw new ValidationException("Variant does not belong to product: " + productVariantId);
        }

        return Optional.of(variant);
    }

    protected void validateProductAvailability(Product product) {
        if (product.isDeleted()) {
            throw new ValidationException("Product is no longer available: " + product.getName());
        }
        if (product.getBasePrice() == null) {
            throw new ValidationException(
                    "Product does not have a fixed price and cannot be ordered online: " + product.getName());
        }
    }

    protected void validateRequestedQuantity(Product product, Optional<ProductVariant> variant, Integer quantity) {
        int availableStock = variant.map(ProductVariant::getStockQuantity).orElse(product.getStockQuantity());
        if (availableStock < quantity) {
            throw new ValidationException(
                    "Insufficient stock for product: " + product.getName() +
                            ". Available: " + availableStock +
                            ", Requested: " + quantity);
        }
    }

    protected Money resolveUnitPrice(Product product, Optional<ProductVariant> variant) {
        return variant.map(productVariant -> product.getBasePrice().add(productVariant.getAdditionalPrice()))
                .orElse(product.getBasePrice());
    }

    protected ProductType mapCategoryToType(ProductCategory category) {
        return switch (category) {
            case PRODUCTOS -> ProductType.BEER;
            case MERCHANDISING -> ProductType.MERCH;
            case ESPECIALES -> ProductType.SPECIAL;
        };
    }
}

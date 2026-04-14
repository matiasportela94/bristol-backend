package com.bristol.application.cart.usecase;

import com.bristol.domain.cart.CartItem;
import com.bristol.domain.cart.ShoppingCart;
import com.bristol.domain.cart.ShoppingCartRepository;
import com.bristol.domain.order.ProductType;
import com.bristol.domain.product.*;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.User;
import com.bristol.domain.user.UserRepository;

import java.util.List;
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
            return resolveSingleAvailableVariant(product, productVariantRepository);
        }

        ProductVariant variant = productVariantRepository.findById(new ProductVariantId(productVariantId))
                .orElseThrow(() -> new ValidationException("Product variant not found: " + productVariantId));

        if (!variant.getProductId().equals(product.getId())) {
            throw new ValidationException("Variant does not belong to product: " + productVariantId);
        }

        return Optional.of(variant);
    }

    private Optional<ProductVariant> resolveSingleAvailableVariant(
            Product product,
            ProductVariantRepository productVariantRepository
    ) {
        List<ProductVariant> allVariants = productVariantRepository.findByProductId(product.getId());
        if (allVariants.size() != 1) {
            return Optional.empty();
        }

        List<ProductVariant> availableVariants = productVariantRepository.findInStockByProductId(product.getId());
        if (availableVariants.size() != 1) {
            return Optional.empty();
        }

        ProductVariant onlyVariant = availableVariants.get(0);
        if (!onlyVariant.getId().equals(allVariants.get(0).getId())) {
            return Optional.empty();
        }

        return Optional.of(onlyVariant);
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
        validateRequestedQuantity(product, variant, quantity, 0);
    }

    protected void validateRequestedQuantity(
            Product product,
            Optional<ProductVariant> variant,
            Integer quantity,
            Integer existingCartQuantity
    ) {
        int availableStock = variant.map(ProductVariant::getStockQuantity).orElse(product.getStockQuantity());
        int requestedQuantity = Math.max(0, existingCartQuantity != null ? existingCartQuantity : 0) + quantity;
        if (availableStock < requestedQuantity) {
            throw new ValidationException(
                    "Insufficient stock for product: " + product.getName() +
                            ". Available: " + availableStock +
                            ", Requested: " + requestedQuantity);
        }
    }

    protected int resolveCartQuantity(ShoppingCart cart, ProductId productId, ProductVariantId productVariantId) {
        if (cart == null) {
            return 0;
        }

        return cart.getItems().stream()
                .filter(item -> isSameProduct(item, productId, productVariantId))
                .mapToInt(CartItem::getQuantity)
                .sum();
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

    private boolean isSameProduct(CartItem item, ProductId productId, ProductVariantId productVariantId) {
        if (!item.getProductId().equals(productId)) {
            return false;
        }

        if (productVariantId == null) {
            return item.getProductVariantId() == null;
        }

        return productVariantId.equals(item.getProductVariantId());
    }
}

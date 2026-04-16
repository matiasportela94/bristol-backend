package com.bristol.application.cart.usecase;

import com.bristol.application.cart.dto.CartDto;
import com.bristol.application.cart.dto.UpdateCartItemQuantityRequest;
import com.bristol.domain.cart.CartItem;
import com.bristol.domain.cart.CartItemId;
import com.bristol.domain.cart.ShoppingCart;
import com.bristol.domain.cart.ShoppingCartRepository;
import com.bristol.domain.product.Product;
import com.bristol.domain.product.ProductRepository;
import com.bristol.domain.product.ProductVariant;
import com.bristol.domain.product.ProductVariantRepository;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.user.User;
import com.bristol.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UpdateCartItemQuantityUseCase extends CartCommandSupport {

    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;
    private final TimeProvider timeProvider;

    @Transactional
    public CartDto execute(String userEmail, String itemId, UpdateCartItemQuantityRequest request) {
        User user = resolveUserByEmail(userEmail, userRepository);
        ShoppingCart cart = shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ValidationException("Cart not found for authenticated user"));

        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getId().equals(new CartItemId(itemId)))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Cart item not found: " + itemId));

        Product product = productRepository.findById(existingItem.getProductId())
                .orElseThrow(() -> new ValidationException("Product not found: " + existingItem.getProductId()));
        Optional<ProductVariant> variant = Optional.ofNullable(existingItem.getProductVariantId())
                .flatMap(productVariantRepository::findById);
        if (existingItem.getProductVariantId() != null && variant.isEmpty()) {
            throw new ValidationException("Product variant not found: " + existingItem.getProductVariantId());
        }

        validateProductAvailability(product);
        validateRequestedQuantity(product, variant, request.getQuantity());

        ShoppingCart updatedCart = cart.updateItemQuantity(existingItem.getId(), request.getQuantity(), timeProvider.now());
        return cartMapper.toDto(shoppingCartRepository.save(updatedCart));
    }
}

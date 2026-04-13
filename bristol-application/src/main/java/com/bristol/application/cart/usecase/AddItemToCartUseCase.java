package com.bristol.application.cart.usecase;

import com.bristol.application.cart.dto.AddCartItemRequest;
import com.bristol.application.cart.dto.CartDto;
import com.bristol.domain.cart.ShoppingCart;
import com.bristol.domain.cart.ShoppingCartRepository;
import com.bristol.domain.product.Product;
import com.bristol.domain.product.ProductRepository;
import com.bristol.domain.product.ProductVariant;
import com.bristol.domain.product.ProductVariantRepository;
import com.bristol.domain.user.User;
import com.bristol.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddItemToCartUseCase extends CartCommandSupport {

    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    @Transactional
    public CartDto execute(String userEmail, AddCartItemRequest request) {
        User user = resolveUserByEmail(userEmail, userRepository);
        ShoppingCart cart = getOrCreateCart(user, shoppingCartRepository);
        Product product = productOrThrow(request.getProductId(), productRepository);
        Optional<ProductVariant> variant = resolveVariant(request.getProductVariantId(), product, productVariantRepository);
        int existingCartQuantity = resolveCartQuantity(
                cart,
                product.getId(),
                variant.map(ProductVariant::getId).orElse(null)
        );

        validateProductAvailability(product);
        validateRequestedQuantity(product, variant, request.getQuantity(), existingCartQuantity);

        ShoppingCart updatedCart = cart.addItem(
                product.getId(),
                variant.map(ProductVariant::getId).orElse(null),
                product.getName(),
                mapCategoryToType(product.getCategory()),
                product.getBeerType(),
                request.getQuantity(),
                resolveUnitPrice(product, variant),
                Instant.now()
        );

        return cartMapper.toDto(shoppingCartRepository.save(updatedCart));
    }
}

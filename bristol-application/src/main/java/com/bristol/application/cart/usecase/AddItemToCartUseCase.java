package com.bristol.application.cart.usecase;

import com.bristol.application.cart.dto.AddCartItemRequest;
import com.bristol.application.cart.dto.CartDto;
import com.bristol.application.product.service.UnifiedProductService;
import com.bristol.domain.cart.ShoppingCart;
import com.bristol.domain.cart.ShoppingCartRepository;
import com.bristol.domain.product.BaseProduct;
import com.bristol.domain.product.ProductVariant;
import com.bristol.domain.product.ProductVariantRepository;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.user.User;
import com.bristol.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddItemToCartUseCase extends CartCommandSupport {

    private final ShoppingCartRepository shoppingCartRepository;
    private final UnifiedProductService unifiedProductService;
    private final ProductVariantRepository productVariantRepository;
    private final CartStockService cartStockService;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;
    private final TimeProvider timeProvider;

    @Transactional
    public CartDto execute(String userEmail, AddCartItemRequest request) {
        User user = resolveUserByEmail(userEmail, userRepository);
        ShoppingCart cart = getOrCreateCart(user, shoppingCartRepository, timeProvider);
        BaseProduct product = productOrThrow(request.getProductId(), unifiedProductService);
        Optional<ProductVariant> variant = resolveVariant(request.getProductVariantId(), product, productVariantRepository);
        int existingCartQuantity = resolveCartQuantity(
                cart,
                product.getId(),
                variant.map(ProductVariant::getId).orElse(null)
        );

        validateProductAvailability(product);
        int availableStock = cartStockService.resolveAvailableStock(product, variant);
        validateRequestedQuantity(availableStock, product.getName(), request.getQuantity(), existingCartQuantity);

        ShoppingCart updatedCart = cart.addItem(
                product.getId(),
                variant.map(ProductVariant::getId).orElse(null),
                product.getName(),
                mapCategoryToType(product.getCategory()),
                product.getBeerType(),
                request.getQuantity(),
                resolveUnitPrice(product, variant),
                timeProvider.now()
        );

        return cartMapper.toDto(shoppingCartRepository.save(updatedCart));
    }
}

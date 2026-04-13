package com.bristol.application.cart.usecase;

import com.bristol.application.cart.dto.AddCartItemRequest;
import com.bristol.application.cart.dto.CartDto;
import com.bristol.domain.cart.ShoppingCart;
import com.bristol.domain.cart.ShoppingCartRepository;
import com.bristol.domain.order.ProductType;
import com.bristol.domain.product.BeerType;
import com.bristol.domain.product.Product;
import com.bristol.domain.product.ProductCategory;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductRepository;
import com.bristol.domain.product.ProductSubcategory;
import com.bristol.domain.product.ProductVariantRepository;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.User;
import com.bristol.domain.user.UserRepository;
import com.bristol.domain.user.UserRole;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AddItemToCartUseCaseTest {

    @Test
    void executeShouldRejectWhenCartAlreadyContainsMaximumAvailableStock() {
        ShoppingCartRepository shoppingCartRepository = mock(ShoppingCartRepository.class);
        ProductRepository productRepository = mock(ProductRepository.class);
        ProductVariantRepository productVariantRepository = mock(ProductVariantRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        CartMapper cartMapper = mock(CartMapper.class);
        AddItemToCartUseCase useCase = new AddItemToCartUseCase(
                shoppingCartRepository,
                productRepository,
                productVariantRepository,
                userRepository,
                cartMapper
        );

        Instant now = Instant.parse("2026-04-13T17:20:00Z");
        User user = User.create("cliente@bristol.com", "hash", "Cliente", "Demo", UserRole.USER, now);
        Product product = Product.create(
                "Six Pack Bristol IPA",
                "Pack de 6 latas",
                ProductCategory.PRODUCTOS,
                ProductSubcategory.SIX_PACK,
                BeerType.IPA,
                Money.of(9200),
                1,
                5,
                now
        );
        ShoppingCart cart = ShoppingCart.create(user.getId(), now).addItem(
                product.getId(),
                null,
                product.getName(),
                ProductType.BEER,
                product.getBeerType(),
                1,
                product.getBasePrice(),
                now
        );

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        AddCartItemRequest request = AddCartItemRequest.builder()
                .productId(product.getId().getValue().toString())
                .quantity(1)
                .build();

        assertThatThrownBy(() -> useCase.execute(user.getEmail(), request))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Insufficient stock for product: Six Pack Bristol IPA. Available: 1, Requested: 2");

        verify(shoppingCartRepository, never()).save(any(ShoppingCart.class));
        verify(cartMapper, never()).toDto(any(ShoppingCart.class));
    }

    @Test
    void executeShouldPersistCartWhenRequestedQuantityFitsRemainingStock() {
        ShoppingCartRepository shoppingCartRepository = mock(ShoppingCartRepository.class);
        ProductRepository productRepository = mock(ProductRepository.class);
        ProductVariantRepository productVariantRepository = mock(ProductVariantRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        CartMapper cartMapper = mock(CartMapper.class);
        AddItemToCartUseCase useCase = new AddItemToCartUseCase(
                shoppingCartRepository,
                productRepository,
                productVariantRepository,
                userRepository,
                cartMapper
        );

        Instant now = Instant.parse("2026-04-13T17:20:00Z");
        User user = User.create("cliente@bristol.com", "hash", "Cliente", "Demo", UserRole.USER, now);
        Product product = Product.create(
                "Six Pack Bristol Pale Ale",
                "Pack de 6 latas",
                ProductCategory.PRODUCTOS,
                ProductSubcategory.SIX_PACK,
                BeerType.PALE_ALE,
                Money.of(7800),
                2,
                5,
                now
        );
        ShoppingCart cart = ShoppingCart.create(user.getId(), now).addItem(
                product.getId(),
                null,
                product.getName(),
                ProductType.BEER,
                product.getBeerType(),
                1,
                product.getBasePrice(),
                now
        );

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cartMapper.toDto(any(ShoppingCart.class))).thenReturn(new CartDto());

        AddCartItemRequest request = AddCartItemRequest.builder()
                .productId(product.getId().getValue().toString())
                .quantity(1)
                .build();

        useCase.execute(user.getEmail(), request);

        verify(shoppingCartRepository).save(any(ShoppingCart.class));
        verify(cartMapper).toDto(any(ShoppingCart.class));
    }
}

package com.bristol.application.cart.usecase;

import com.bristol.application.cart.dto.CartDto;
import com.bristol.domain.cart.ShoppingCart;
import com.bristol.domain.cart.ShoppingCartRepository;
import com.bristol.domain.order.ProductType;
import com.bristol.domain.product.BeerType;
import com.bristol.domain.product.Product;
import com.bristol.domain.product.ProductCategory;
import com.bristol.domain.product.ProductRepository;
import com.bristol.domain.product.ProductSubcategory;
import com.bristol.domain.product.ProductVariantRepository;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.User;
import com.bristol.domain.user.UserRepository;
import com.bristol.domain.user.UserRole;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetMyCartUseCaseTest {

    @Test
    void executeShouldPersistReconciledCartBeforeMapping() {
        ShoppingCartRepository shoppingCartRepository = mock(ShoppingCartRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        CartMapper cartMapper = mock(CartMapper.class);
        ProductRepository productRepository = mock(ProductRepository.class);
        ProductVariantRepository productVariantRepository = mock(ProductVariantRepository.class);

        GetMyCartUseCase useCase = new GetMyCartUseCase(
                shoppingCartRepository,
                userRepository,
                cartMapper,
                new CartReconciliationService(productRepository, productVariantRepository),
                fixedTimeProvider()
        );

        Instant now = Instant.parse("2026-04-10T20:00:00Z");
        User user = User.create("demo.user@bristol.com", "hash", "Demo", "User", UserRole.USER, now);
        ShoppingCart cart = ShoppingCart.create(user.getId(), now).addItem(
                com.bristol.domain.product.ProductId.generate(),
                null,
                "Lata Bristol IPA",
                ProductType.BEER,
                BeerType.IPA,
                1,
                Money.of(100),
                now
        );

        Product product = mock(Product.class);
        when(product.isDeleted()).thenReturn(false);
        when(product.getBasePrice()).thenReturn(Money.of(120));
        when(product.getStockQuantity()).thenReturn(10);
        when(product.getName()).thenReturn("Lata Bristol IPA");
        when(product.getCategory()).thenReturn(ProductCategory.PRODUCTOS);
        when(product.getSubcategory()).thenReturn(ProductSubcategory.CAN);
        when(product.getBeerType()).thenReturn(BeerType.IPA);

        when(userRepository.findByEmail("demo.user@bristol.com")).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(productRepository.findById(cart.getItems().get(0).getProductId())).thenReturn(Optional.of(product));
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cartMapper.toDto(any(ShoppingCart.class))).thenReturn(new CartDto());

        useCase.execute("demo.user@bristol.com");

        verify(shoppingCartRepository).save(any(ShoppingCart.class));
        verify(cartMapper).toDto(any(ShoppingCart.class));
    }

    private static TimeProvider fixedTimeProvider() {
        Instant fixedInstant = Instant.parse("2026-04-10T20:00:00Z");
        return new TimeProvider() {
            @Override
            public Instant now() {
                return fixedInstant;
            }

            @Override
            public LocalDateTime nowDateTime() {
                return LocalDateTime.of(2026, 4, 10, 17, 0);
            }

            @Override
            public LocalDate nowDate() {
                return LocalDate.of(2026, 4, 10);
            }
        };
    }
}

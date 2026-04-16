package com.bristol.application.cart.usecase;

import com.bristol.application.cart.dto.CheckoutCartRequest;
import com.bristol.application.cart.dto.CheckoutCartResponse;
import com.bristol.application.cart.dto.CartAdjustmentType;
import com.bristol.application.order.service.StockManagementService;
import com.bristol.application.order.usecase.OrderMapper;
import com.bristol.application.order.usecase.OrderPromotionApplicationService;
import com.bristol.domain.address.UserAddress;
import com.bristol.domain.address.UserAddressId;
import com.bristol.domain.address.UserAddressRepository;
import com.bristol.domain.cart.ShoppingCart;
import com.bristol.domain.cart.ShoppingCartRepository;
import com.bristol.domain.coupon.CouponId;
import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderRepository;
import com.bristol.domain.order.ProductType;
import com.bristol.domain.product.BeerType;
import com.bristol.domain.product.Product;
import com.bristol.domain.product.ProductCategory;
import com.bristol.domain.product.ProductRepository;
import com.bristol.domain.product.ProductSubcategory;
import com.bristol.domain.product.ProductVariantRepository;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.User;
import com.bristol.domain.user.UserRepository;
import com.bristol.domain.user.UserRole;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CheckoutCartUseCaseTest {

    @Test
    void executeShouldApplyRequestedOrderCouponBeforeSavingOrder() {
        ShoppingCartRepository shoppingCartRepository = mock(ShoppingCartRepository.class);
        ProductRepository productRepository = mock(ProductRepository.class);
        ProductVariantRepository productVariantRepository = mock(ProductVariantRepository.class);
        UserAddressRepository userAddressRepository = mock(UserAddressRepository.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        OrderPromotionApplicationService orderPromotionApplicationService = mock(OrderPromotionApplicationService.class);
        CartPricingPreviewService cartPricingPreviewService = mock(CartPricingPreviewService.class);
        StockManagementService stockManagementService = mock(StockManagementService.class);

        CheckoutCartUseCase useCase = new CheckoutCartUseCase(
                shoppingCartRepository,
                productRepository,
                productVariantRepository,
                userAddressRepository,
                orderRepository,
                userRepository,
                new CartMapper(cartPricingPreviewService),
                new OrderMapper(),
                orderPromotionApplicationService,
                new CartReconciliationService(productRepository, productVariantRepository),
                stockManagementService,
                fixedTimeProvider()
        );

        Instant now = Instant.parse("2026-04-09T12:00:00Z");
        User user = User.create("buyer@example.com", "hash", "Buyer", "User", UserRole.USER, now);
        UserAddress address = UserAddress.create(
                user.getId(),
                "Calle 123",
                null,
                "CABA",
                "Buenos Aires",
                "1000",
                DeliveryZoneId.generate(),
                true,
                now
        );
        ShoppingCart cart = ShoppingCart.create(user.getId(), now).addItem(
                com.bristol.domain.product.ProductId.generate(),
                null,
                "IPA",
                ProductType.BEER,
                BeerType.IPA,
                1,
                Money.of(100),
                now
        );

        Product product = mock(Product.class);
        when(product.isDeleted()).thenReturn(false);
        when(product.getBasePrice()).thenReturn(Money.of(100));
        when(product.getStockQuantity()).thenReturn(10);
        when(product.getId()).thenReturn(cart.getItems().get(0).getProductId());
        when(product.getName()).thenReturn("IPA");
        when(product.getCategory()).thenReturn(ProductCategory.PRODUCTOS);
        when(product.getSubcategory()).thenReturn(ProductSubcategory.CAN);
        when(product.getBeerType()).thenReturn(BeerType.IPA);

        when(userRepository.findByEmail("buyer@example.com")).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(userAddressRepository.findById(new UserAddressId(address.getId().getValue()))).thenReturn(Optional.of(address));
        when(productRepository.findById(cart.getItems().get(0).getProductId())).thenReturn(Optional.of(product));
        when(cartPricingPreviewService.preview(any(ShoppingCart.class))).thenReturn(CartPricingPreviewService.CartPricingPreview.empty());
        when(cartPricingPreviewService.preview(any(ShoppingCart.class), any())).thenReturn(CartPricingPreviewService.CartPricingPreview.empty());
        when(orderPromotionApplicationService.applyRequestedPromotion(any(Order.class), eq("ORDER10")))
                .thenAnswer(invocation -> ((Order) invocation.getArgument(0))
                        .applyOrderDiscount(CouponId.generate(), Money.of(10), now));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CheckoutCartResponse response = useCase.execute("buyer@example.com", CheckoutCartRequest.builder()
                .shippingAddressId(address.getId().getValue().toString())
                .couponCode("ORDER10")
                .build());

        assertThat(response.isCheckoutSucceeded()).isTrue();
        assertThat(response.getCreatedOrder()).isNotNull();
        assertThat(response.getCreatedOrder().getOrderDiscountAmount()).isEqualByComparingTo("10.00");
        assertThat(response.getCreatedOrder().getTotal()).isEqualByComparingTo("90.00");
        assertThat(response.getCreatedOrder().isStockUpdated()).isTrue();
        assertThat(response.getCart().getItems()).isEmpty();
        verify(orderPromotionApplicationService).applyRequestedPromotion(any(Order.class), eq("ORDER10"));
        verify(stockManagementService).deductStockForOrder(any(Order.class));
    }

    @Test
    void executeShouldReturnCheckoutFailureWhenRequestedCouponIsInvalid() {
        ShoppingCartRepository shoppingCartRepository = mock(ShoppingCartRepository.class);
        ProductRepository productRepository = mock(ProductRepository.class);
        ProductVariantRepository productVariantRepository = mock(ProductVariantRepository.class);
        UserAddressRepository userAddressRepository = mock(UserAddressRepository.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        OrderPromotionApplicationService orderPromotionApplicationService = mock(OrderPromotionApplicationService.class);
        CartPricingPreviewService cartPricingPreviewService = mock(CartPricingPreviewService.class);
        StockManagementService stockManagementService = mock(StockManagementService.class);

        CheckoutCartUseCase useCase = new CheckoutCartUseCase(
                shoppingCartRepository,
                productRepository,
                productVariantRepository,
                userAddressRepository,
                orderRepository,
                userRepository,
                new CartMapper(cartPricingPreviewService),
                new OrderMapper(),
                orderPromotionApplicationService,
                new CartReconciliationService(productRepository, productVariantRepository),
                stockManagementService,
                fixedTimeProvider()
        );

        Instant now = Instant.parse("2026-04-09T12:00:00Z");
        User user = User.create("buyer@example.com", "hash", "Buyer", "User", UserRole.USER, now);
        UserAddress address = UserAddress.create(
                user.getId(),
                "Calle 123",
                null,
                "CABA",
                "Buenos Aires",
                "1000",
                DeliveryZoneId.generate(),
                true,
                now
        );
        ShoppingCart cart = ShoppingCart.create(user.getId(), now).addItem(
                com.bristol.domain.product.ProductId.generate(),
                null,
                "IPA",
                ProductType.BEER,
                BeerType.IPA,
                1,
                Money.of(100),
                now
        );

        Product product = mock(Product.class);
        when(product.isDeleted()).thenReturn(false);
        when(product.getBasePrice()).thenReturn(Money.of(100));
        when(product.getStockQuantity()).thenReturn(10);
        when(product.getId()).thenReturn(cart.getItems().get(0).getProductId());
        when(product.getName()).thenReturn("IPA");
        when(product.getCategory()).thenReturn(ProductCategory.PRODUCTOS);
        when(product.getSubcategory()).thenReturn(ProductSubcategory.CAN);
        when(product.getBeerType()).thenReturn(BeerType.IPA);

        when(userRepository.findByEmail("buyer@example.com")).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(userAddressRepository.findById(new UserAddressId(address.getId().getValue()))).thenReturn(Optional.of(address));
        when(productRepository.findById(cart.getItems().get(0).getProductId())).thenReturn(Optional.of(product));
        when(cartPricingPreviewService.preview(any(ShoppingCart.class))).thenReturn(CartPricingPreviewService.CartPricingPreview.empty());
        when(cartPricingPreviewService.preview(any(ShoppingCart.class), any())).thenReturn(CartPricingPreviewService.CartPricingPreview.empty());
        when(orderPromotionApplicationService.applyRequestedPromotion(any(Order.class), eq("BADCODE")))
                .thenThrow(new ValidationException("Invalid coupon code"));

        CheckoutCartResponse response = useCase.execute("buyer@example.com", CheckoutCartRequest.builder()
                .shippingAddressId(address.getId().getValue().toString())
                .couponCode("BADCODE")
                .build());

        assertThat(response.isCheckoutSucceeded()).isFalse();
        assertThat(response.getCreatedOrder()).isNull();
        assertThat(response.getMessage()).isEqualTo("Invalid coupon code");
        assertThat(response.getCart().getItems()).hasSize(1);
        assertThat(response.getAdjustments()).hasSize(1);
        assertThat(response.getAdjustments().get(0).getType()).isEqualTo(CartAdjustmentType.PROMOTION_REMOVED);
        assertThat(response.getAdjustments().get(0).getPreviousValue()).isEqualTo("BADCODE");
        verify(orderRepository, never()).save(any(Order.class));
        verify(stockManagementService, never()).deductStockForOrder(any(Order.class));
    }

    private static TimeProvider fixedTimeProvider() {
        Instant fixedInstant = Instant.parse("2026-04-09T12:00:00Z");
        return new TimeProvider() {
            @Override
            public Instant now() {
                return fixedInstant;
            }

            @Override
            public LocalDateTime nowDateTime() {
                return LocalDateTime.of(2026, 4, 9, 9, 0);
            }

            @Override
            public LocalDate nowDate() {
                return LocalDate.of(2026, 4, 9);
            }
        };
    }
}

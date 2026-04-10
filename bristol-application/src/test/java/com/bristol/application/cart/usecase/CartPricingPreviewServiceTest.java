package com.bristol.application.cart.usecase;

import com.bristol.domain.cart.ShoppingCart;
import com.bristol.domain.coupon.Coupon;
import com.bristol.domain.coupon.CouponAppliesTo;
import com.bristol.domain.coupon.CouponCustomerEligibility;
import com.bristol.domain.coupon.CouponDiscountType;
import com.bristol.domain.coupon.CouponMethod;
import com.bristol.domain.coupon.CouponRedemptionRepository;
import com.bristol.domain.coupon.CouponRepository;
import com.bristol.domain.coupon.CouponStatus;
import com.bristol.domain.coupon.CouponTriggerType;
import com.bristol.domain.coupon.CouponValueType;
import com.bristol.domain.order.ProductType;
import com.bristol.domain.product.BeerType;
import com.bristol.domain.product.Product;
import com.bristol.domain.product.ProductCategory;
import com.bristol.domain.product.ProductRepository;
import com.bristol.domain.product.ProductSubcategory;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.User;
import com.bristol.domain.user.UserRepository;
import com.bristol.domain.user.UserRole;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CartPricingPreviewServiceTest {

    @Test
    void previewShouldApplyAutomaticProductPromotionToCartTotals() {
        ProductRepository productRepository = mock(ProductRepository.class);
        CouponRepository couponRepository = mock(CouponRepository.class);
        CouponRedemptionRepository couponRedemptionRepository = mock(CouponRedemptionRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        Instant now = Instant.parse("2026-04-10T17:00:00Z");
        TimeProvider timeProvider = new TimeProvider() {
            @Override
            public Instant now() {
                return now;
            }

            @Override
            public LocalDateTime nowDateTime() {
                return LocalDateTime.ofInstant(now, java.time.ZoneOffset.UTC);
            }

            @Override
            public LocalDate nowDate() {
                return LocalDate.of(2026, 4, 10);
            }
        };

        CartPricingPreviewService service = new CartPricingPreviewService(
                productRepository,
                couponRepository,
                couponRedemptionRepository,
                timeProvider,
                userRepository
        );

        User user = User.create("buyer@example.com", "hash", "Buyer", "User", UserRole.USER, now);
        ShoppingCart cart = ShoppingCart.create(user.getId(), now).addItem(
                com.bristol.domain.product.ProductId.generate(),
                null,
                "Lata Bristol IPA",
                ProductType.BEER,
                BeerType.IPA,
                3,
                Money.of(1600),
                now
        );

        Product product = mock(Product.class);
        when(product.getCategory()).thenReturn(ProductCategory.PRODUCTOS);
        when(product.getSubcategory()).thenReturn(ProductSubcategory.CAN);
        when(productRepository.findById(cart.getItems().get(0).getProductId())).thenReturn(Optional.of(product));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        Coupon coupon = Coupon.create(
                "2x1 IPA",
                null,
                "Promoción automática 2x1 para IPAs",
                CouponMethod.AUTOMATIC,
                CouponDiscountType.PRODUCT,
                CouponValueType.PERCENTAGE,
                BigDecimal.valueOf(100),
                CouponAppliesTo.SPECIFIC_PRODUCTS,
                LocalDate.of(2026, 4, 9),
                null,
                user.getId(),
                now
        ).reconfigure(
                "2x1 IPA",
                null,
                "Promoción automática 2x1 para IPAs",
                CouponMethod.AUTOMATIC,
                CouponDiscountType.PRODUCT,
                CouponValueType.PERCENTAGE,
                BigDecimal.valueOf(100),
                CouponAppliesTo.SPECIFIC_PRODUCTS,
                "[{\"beerType\":\"IPA\"}]",
                CouponCustomerEligibility.EVERYONE,
                null,
                null,
                false,
                null,
                false,
                null,
                false,
                true,
                true,
                LocalDate.of(2026, 4, 9),
                null,
                false,
                null,
                null,
                CouponStatus.ACTIVE,
                50,
                CouponTriggerType.BUY_X_GET_Y,
                null,
                null,
                false,
                "[]",
                "{\"buyX\":2,\"getY\":1}",
                now
        );

        when(couponRepository.findAutomatic()).thenReturn(List.of(coupon));

        CartPricingPreviewService.CartPricingPreview preview = service.preview(cart);

        assertThat(preview.originalSubtotal()).isEqualByComparingTo("4800");
        assertThat(preview.productDiscountAmount()).isEqualByComparingTo("1600");
        assertThat(preview.subtotal()).isEqualByComparingTo("3200");
        assertThat(preview.orderDiscountAmount()).isEqualByComparingTo("0");
        assertThat(preview.total()).isEqualByComparingTo("3200");
        assertThat(preview.appliedProductPromotion()).isPresent();
        assertThat(preview.appliedProductPromotion().orElseThrow().getBadgeText()).isEqualTo("3x2");
        assertThat(preview.findItem(cart.getItems().get(0).getProductId().getValue().toString(), null))
                .isPresent()
                .get()
                .extracting(item -> item.getItemDiscountAmount().getAmount(), item -> item.getSubtotal().getAmount())
                .containsExactly(new BigDecimal("1600.00"), new BigDecimal("3200.00"));
    }

    @Test
    void previewShouldApplyRequestedOrderCouponAfterAutomaticProductPromotion() {
        ProductRepository productRepository = mock(ProductRepository.class);
        CouponRepository couponRepository = mock(CouponRepository.class);
        CouponRedemptionRepository couponRedemptionRepository = mock(CouponRedemptionRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        Instant now = Instant.parse("2026-04-10T17:00:00Z");
        TimeProvider timeProvider = new TimeProvider() {
            @Override
            public Instant now() {
                return now;
            }

            @Override
            public LocalDateTime nowDateTime() {
                return LocalDateTime.ofInstant(now, java.time.ZoneOffset.UTC);
            }

            @Override
            public LocalDate nowDate() {
                return LocalDate.of(2026, 4, 10);
            }
        };

        CartPricingPreviewService service = new CartPricingPreviewService(
                productRepository,
                couponRepository,
                couponRedemptionRepository,
                timeProvider,
                userRepository
        );

        User user = User.create("buyer@example.com", "hash", "Buyer", "User", UserRole.USER, now);
        ShoppingCart cart = ShoppingCart.create(user.getId(), now).addItem(
                com.bristol.domain.product.ProductId.generate(),
                null,
                "Lata Bristol IPA",
                ProductType.BEER,
                BeerType.IPA,
                3,
                Money.of(1600),
                now
        );

        Product product = mock(Product.class);
        when(product.getCategory()).thenReturn(ProductCategory.PRODUCTOS);
        when(product.getSubcategory()).thenReturn(ProductSubcategory.CAN);
        when(productRepository.findById(cart.getItems().get(0).getProductId())).thenReturn(Optional.of(product));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        Coupon automaticPromotion = Coupon.create(
                "2x1 IPA",
                null,
                "PromociÃ³n automÃ¡tica 2x1 para IPAs",
                CouponMethod.AUTOMATIC,
                CouponDiscountType.PRODUCT,
                CouponValueType.PERCENTAGE,
                BigDecimal.valueOf(100),
                CouponAppliesTo.SPECIFIC_PRODUCTS,
                LocalDate.of(2026, 4, 9),
                null,
                user.getId(),
                now
        ).reconfigure(
                "2x1 IPA",
                null,
                "PromociÃ³n automÃ¡tica 2x1 para IPAs",
                CouponMethod.AUTOMATIC,
                CouponDiscountType.PRODUCT,
                CouponValueType.PERCENTAGE,
                BigDecimal.valueOf(100),
                CouponAppliesTo.SPECIFIC_PRODUCTS,
                "[{\"beerType\":\"IPA\"}]",
                CouponCustomerEligibility.EVERYONE,
                null,
                null,
                false,
                null,
                false,
                null,
                false,
                true,
                true,
                LocalDate.of(2026, 4, 9),
                null,
                false,
                null,
                null,
                CouponStatus.ACTIVE,
                50,
                CouponTriggerType.BUY_X_GET_Y,
                null,
                null,
                false,
                "[]",
                "{\"buyX\":2,\"getY\":1}",
                now
        );

        Coupon orderCoupon = Coupon.create(
                "10% Pedido",
                "ORDER10",
                "CupÃ³n del 10% sobre el subtotal",
                CouponMethod.CODE,
                CouponDiscountType.ORDER,
                CouponValueType.PERCENTAGE,
                BigDecimal.TEN,
                CouponAppliesTo.ENTIRE_ORDER,
                LocalDate.of(2026, 4, 9),
                null,
                user.getId(),
                now
        ).reconfigure(
                "10% Pedido",
                "ORDER10",
                "CupÃ³n del 10% sobre el subtotal",
                CouponMethod.CODE,
                CouponDiscountType.ORDER,
                CouponValueType.PERCENTAGE,
                BigDecimal.TEN,
                CouponAppliesTo.ENTIRE_ORDER,
                "[]",
                CouponCustomerEligibility.EVERYONE,
                null,
                null,
                false,
                null,
                false,
                null,
                true,
                true,
                true,
                LocalDate.of(2026, 4, 9),
                null,
                false,
                null,
                null,
                CouponStatus.ACTIVE,
                40,
                CouponTriggerType.NONE,
                null,
                null,
                false,
                "[]",
                "{}",
                now
        );

        when(couponRepository.findAutomatic()).thenReturn(List.of(automaticPromotion));
        when(couponRepository.findByCode("ORDER10")).thenReturn(Optional.of(orderCoupon));

        CartPricingPreviewService.CartPricingPreview preview = service.preview(cart, "ORDER10");

        assertThat(preview.originalSubtotal()).isEqualByComparingTo("4800");
        assertThat(preview.productDiscountAmount()).isEqualByComparingTo("1600");
        assertThat(preview.subtotal()).isEqualByComparingTo("3200");
        assertThat(preview.orderDiscountAmount()).isEqualByComparingTo("320");
        assertThat(preview.total()).isEqualByComparingTo("2880");
        assertThat(preview.appliedOrderPromotion()).isNotNull();
        assertThat(preview.appliedOrderPromotion().getCode()).isEqualTo("ORDER10");
    }

    @Test
    void previewShouldApplyMultipleAutomaticProductPromotionsToDifferentItems() {
        ProductRepository productRepository = mock(ProductRepository.class);
        CouponRepository couponRepository = mock(CouponRepository.class);
        CouponRedemptionRepository couponRedemptionRepository = mock(CouponRedemptionRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        Instant now = Instant.parse("2026-04-10T17:00:00Z");
        TimeProvider timeProvider = new TimeProvider() {
            @Override
            public Instant now() {
                return now;
            }

            @Override
            public LocalDateTime nowDateTime() {
                return LocalDateTime.ofInstant(now, java.time.ZoneOffset.UTC);
            }

            @Override
            public LocalDate nowDate() {
                return LocalDate.of(2026, 4, 10);
            }
        };

        CartPricingPreviewService service = new CartPricingPreviewService(
                productRepository,
                couponRepository,
                couponRedemptionRepository,
                timeProvider,
                userRepository
        );

        User user = User.create("buyer@example.com", "hash", "Buyer", "User", UserRole.USER, now);
        ShoppingCart cart = ShoppingCart.create(user.getId(), now)
                .addItem(
                        com.bristol.domain.product.ProductId.generate(),
                        null,
                        "Lata Bristol IPA",
                        ProductType.BEER,
                        BeerType.IPA,
                        5,
                        Money.of(1600),
                        now
                )
                .addItem(
                        com.bristol.domain.product.ProductId.generate(),
                        null,
                        "Remera Bristol",
                        ProductType.MERCH,
                        null,
                        1,
                        Money.of(45000),
                        now
                );

        Product beerProduct = mock(Product.class);
        when(beerProduct.getCategory()).thenReturn(ProductCategory.PRODUCTOS);
        when(beerProduct.getSubcategory()).thenReturn(ProductSubcategory.CAN);

        Product merchProduct = mock(Product.class);
        when(merchProduct.getCategory()).thenReturn(ProductCategory.MERCHANDISING);
        when(merchProduct.getSubcategory()).thenReturn(ProductSubcategory.REMERA);

        when(productRepository.findById(cart.getItems().get(0).getProductId())).thenReturn(Optional.of(beerProduct));
        when(productRepository.findById(cart.getItems().get(1).getProductId())).thenReturn(Optional.of(merchProduct));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        Coupon ipaPromotion = Coupon.create(
                "IPA 3x2",
                null,
                "Promoción automática 3x2 para IPAs",
                CouponMethod.AUTOMATIC,
                CouponDiscountType.PRODUCT,
                CouponValueType.PERCENTAGE,
                BigDecimal.valueOf(100),
                CouponAppliesTo.SPECIFIC_PRODUCTS,
                LocalDate.of(2026, 4, 9),
                null,
                user.getId(),
                now
        ).reconfigure(
                "IPA 3x2",
                null,
                "Promoción automática 3x2 para IPAs",
                CouponMethod.AUTOMATIC,
                CouponDiscountType.PRODUCT,
                CouponValueType.PERCENTAGE,
                BigDecimal.valueOf(100),
                CouponAppliesTo.SPECIFIC_PRODUCTS,
                "[{\"beerType\":\"IPA\"}]",
                CouponCustomerEligibility.EVERYONE,
                null,
                null,
                false,
                null,
                false,
                null,
                false,
                true,
                true,
                LocalDate.of(2026, 4, 9),
                null,
                false,
                null,
                null,
                CouponStatus.ACTIVE,
                50,
                CouponTriggerType.BUY_X_GET_Y,
                null,
                null,
                false,
                "[]",
                "{\"buyX\":2,\"getY\":1}",
                now
        );

        Coupon merchPromotion = Coupon.create(
                "10% Merch",
                null,
                "Promoción automática 10% en merchandising",
                CouponMethod.AUTOMATIC,
                CouponDiscountType.PRODUCT,
                CouponValueType.PERCENTAGE,
                BigDecimal.TEN,
                CouponAppliesTo.SPECIFIC_PRODUCTS,
                LocalDate.of(2026, 4, 9),
                null,
                user.getId(),
                now
        ).reconfigure(
                "10% Merch",
                null,
                "Promoción automática 10% en merchandising",
                CouponMethod.AUTOMATIC,
                CouponDiscountType.PRODUCT,
                CouponValueType.PERCENTAGE,
                BigDecimal.TEN,
                CouponAppliesTo.SPECIFIC_PRODUCTS,
                "[{\"category\":\"MERCHANDISING\"}]",
                CouponCustomerEligibility.EVERYONE,
                null,
                null,
                false,
                null,
                false,
                null,
                false,
                true,
                true,
                LocalDate.of(2026, 4, 9),
                null,
                false,
                null,
                null,
                CouponStatus.ACTIVE,
                60,
                CouponTriggerType.NONE,
                null,
                null,
                false,
                "[]",
                "{}",
                now
        );

        when(couponRepository.findAutomatic()).thenReturn(List.of(merchPromotion, ipaPromotion));

        CartPricingPreviewService.CartPricingPreview preview = service.preview(cart);

        assertThat(preview.originalSubtotal()).isEqualByComparingTo("53000");
        assertThat(preview.productDiscountAmount()).isEqualByComparingTo("6100");
        assertThat(preview.subtotal()).isEqualByComparingTo("46900");
        assertThat(preview.findAppliedProductPromotion(ipaPromotion.getId().getValue().toString())).isPresent();
        assertThat(preview.findAppliedProductPromotion(merchPromotion.getId().getValue().toString())).isPresent();
        assertThat(preview.findItem(cart.getItems().get(0).getProductId().getValue().toString(), null))
                .isPresent()
                .get()
                .extracting(item -> item.getItemDiscountAmount().getAmount(), item -> item.getSubtotal().getAmount())
                .containsExactly(new BigDecimal("1600.00"), new BigDecimal("6400.00"));
        assertThat(preview.findItem(cart.getItems().get(1).getProductId().getValue().toString(), null))
                .isPresent()
                .get()
                .extracting(item -> item.getItemDiscountAmount().getAmount(), item -> item.getSubtotal().getAmount())
                .containsExactly(new BigDecimal("4500.00"), new BigDecimal("40500.00"));
    }
}

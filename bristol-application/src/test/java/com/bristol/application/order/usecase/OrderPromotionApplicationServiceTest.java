package com.bristol.application.order.usecase;

import com.bristol.application.order.dto.OrderDto;
import com.bristol.domain.coupon.Coupon;
import com.bristol.domain.coupon.CouponAppliesTo;
import com.bristol.domain.coupon.CouponCustomerEligibility;
import com.bristol.domain.coupon.CouponDiscountType;
import com.bristol.domain.coupon.CouponMethod;
import com.bristol.domain.coupon.CouponRepository;
import com.bristol.domain.coupon.CouponRedemptionRepository;
import com.bristol.domain.coupon.CouponStatus;
import com.bristol.domain.coupon.CouponTriggerType;
import com.bristol.domain.coupon.CouponValueType;
import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderId;
import com.bristol.domain.order.OrderItem;
import com.bristol.domain.order.OrderRepository;
import com.bristol.domain.order.OrderStatus;
import com.bristol.domain.order.ProductType;
import com.bristol.domain.order.ShippingAddress;
import com.bristol.domain.product.BeerType;
import com.bristol.domain.product.ProductCategory;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductSubcategory;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.User;
import com.bristol.domain.user.UserId;
import com.bristol.domain.user.UserRepository;
import com.bristol.domain.user.UserRole;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderPromotionApplicationServiceTest {

    @Test
    void applyCouponShouldDropIncompatibleLowerPriorityShippingPromotion() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        CouponRepository couponRepository = mock(CouponRepository.class);
        CouponRedemptionRepository couponRedemptionRepository = mock(CouponRedemptionRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        OrderMapper orderMapper = new OrderMapper();
        TimeProvider timeProvider = fixedTimeProvider();

        OrderPromotionApplicationService service = new OrderPromotionApplicationService(
                orderRepository,
                couponRepository,
                couponRedemptionRepository,
                orderMapper,
                timeProvider,
                userRepository
        );

        Coupon existingShippingCoupon = sampleCoupon("SHIP50", CouponDiscountType.SHIPPING, 2, false, false);
        Order orderWithShippingCoupon = sampleOrder().applyShippingDiscount(
                existingShippingCoupon.getId(),
                Money.of(50),
                timeProvider.now()
        );
        Coupon requestedOrderCoupon = sampleCoupon("ORDER10", CouponDiscountType.ORDER, 10, false, false);

        when(orderRepository.findById(orderWithShippingCoupon.getId())).thenReturn(Optional.of(orderWithShippingCoupon));
        when(userRepository.findById(orderWithShippingCoupon.getUserId())).thenReturn(Optional.of(sampleUser(orderWithShippingCoupon.getUserId(), "buyer@example.com")));
        when(couponRepository.findByCode("ORDER10")).thenReturn(Optional.of(requestedOrderCoupon));
        when(couponRepository.findById(orderWithShippingCoupon.getShippingDiscountCouponId())).thenReturn(Optional.of(existingShippingCoupon));
        when(couponRepository.findAutomatic()).thenReturn(List.of());
        when(couponRedemptionRepository.countByCouponIdAndUserId(any(), any())).thenReturn(0L);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderDto result = service.applyCoupon(orderWithShippingCoupon.getId().getValue().toString(), "ORDER10", CouponDiscountType.ORDER);

        assertThat(result.getOrderDiscountAmount()).isEqualByComparingTo("20.00");
        assertThat(result.getShippingDiscountAmount()).isEqualByComparingTo("0.00");
        assertThat(result.getTotal()).isEqualByComparingTo("280.00");
    }

    @Test
    void applyRequestedPromotionsShouldIncludeAutomaticCoupons() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        CouponRepository couponRepository = mock(CouponRepository.class);
        CouponRedemptionRepository couponRedemptionRepository = mock(CouponRedemptionRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        OrderMapper orderMapper = new OrderMapper();
        TimeProvider timeProvider = fixedTimeProvider();

        OrderPromotionApplicationService service = new OrderPromotionApplicationService(
                orderRepository,
                couponRepository,
                couponRedemptionRepository,
                orderMapper,
                timeProvider,
                userRepository
        );

        Order order = sampleOrder();
        when(userRepository.findById(order.getUserId())).thenReturn(Optional.of(sampleUser(order.getUserId(), "buyer@example.com")));
        Coupon automaticShippingCoupon = sampleAutomaticCoupon("AUTOSHIP", CouponDiscountType.SHIPPING, 5);

        when(couponRepository.findAutomatic()).thenReturn(List.of(automaticShippingCoupon));
        when(couponRedemptionRepository.countByCouponIdAndUserId(any(), any())).thenReturn(0L);

        Order repriced = service.applyRequestedPromotions(order, null, null);

        assertThat(repriced.getShippingDiscountCouponId()).isEqualTo(automaticShippingCoupon.getId());
        assertThat(repriced.getShippingDiscountAmount().getAmount()).isEqualByComparingTo("50.00");
        assertThat(repriced.getTotal().getAmount()).isEqualByComparingTo("250.00");
    }

    @Test
    void applyRequestedPromotionsShouldApplyAutomaticProductCouponToMatchingLine() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        CouponRepository couponRepository = mock(CouponRepository.class);
        CouponRedemptionRepository couponRedemptionRepository = mock(CouponRedemptionRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        OrderMapper orderMapper = new OrderMapper();
        TimeProvider timeProvider = fixedTimeProvider();

        OrderPromotionApplicationService service = new OrderPromotionApplicationService(
                orderRepository,
                couponRepository,
                couponRedemptionRepository,
                orderMapper,
                timeProvider,
                userRepository
        );

        Order order = sampleOrder();
        when(userRepository.findById(order.getUserId())).thenReturn(Optional.of(sampleUser(order.getUserId(), "buyer@example.com")));
        Coupon productCoupon = Coupon.create(
                "IPA20",
                null,
                "IPA20",
                CouponMethod.AUTOMATIC,
                CouponDiscountType.PRODUCT,
                CouponValueType.PERCENTAGE,
                BigDecimal.valueOf(20),
                CouponAppliesTo.SPECIFIC_PRODUCTS,
                LocalDate.parse("2026-04-07"),
                LocalTime.MIDNIGHT,
                UserId.generate(),
                Instant.parse("2026-04-07T12:00:00Z")
        ).reconfigure(
                "IPA20",
                null,
                "IPA20",
                CouponMethod.AUTOMATIC,
                CouponDiscountType.PRODUCT,
                CouponValueType.PERCENTAGE,
                BigDecimal.valueOf(20),
                CouponAppliesTo.SPECIFIC_PRODUCTS,
                "[{\"productId\":\"" + order.getItems().get(0).getProductId().getValue() + "\"}]",
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
                LocalDate.parse("2026-04-07"),
                LocalTime.MIDNIGHT,
                false,
                null,
                null,
                CouponStatus.ACTIVE,
                10,
                CouponTriggerType.NONE,
                null,
                null,
                false,
                "[]",
                "{}",
                Instant.parse("2026-04-07T12:00:00Z")
        );

        when(couponRepository.findAutomatic()).thenReturn(List.of(productCoupon));
        when(couponRedemptionRepository.countByCouponIdAndUserId(any(), any())).thenReturn(0L);

        Order repriced = service.applyRequestedPromotions(order, null, null);

        assertThat(repriced.getItems().get(0).getItemDiscountCouponId()).isEqualTo(productCoupon.getId());
        assertThat(repriced.getItems().get(0).getItemDiscountAmount().getAmount()).isEqualByComparingTo("40.00");
        assertThat(repriced.getSubtotal().getAmount()).isEqualByComparingTo("160.00");
        assertThat(repriced.getTotal().getAmount()).isEqualByComparingTo("260.00");
    }

    @Test
    void applyRequestedPromotionsShouldApplyAutomaticProductCouponByCategory() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        CouponRepository couponRepository = mock(CouponRepository.class);
        CouponRedemptionRepository couponRedemptionRepository = mock(CouponRedemptionRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        OrderMapper orderMapper = new OrderMapper();
        TimeProvider timeProvider = fixedTimeProvider();

        OrderPromotionApplicationService service = new OrderPromotionApplicationService(
                orderRepository,
                couponRepository,
                couponRedemptionRepository,
                orderMapper,
                timeProvider,
                userRepository
        );

        Order order = sampleOrder(ProductCategory.MERCHANDISING, ProductSubcategory.REMERA);
        when(userRepository.findById(order.getUserId())).thenReturn(Optional.of(sampleUser(order.getUserId(), "buyer@example.com")));
        Coupon categoryCoupon = Coupon.create(
                "MERCH15",
                null,
                "MERCH15",
                CouponMethod.AUTOMATIC,
                CouponDiscountType.PRODUCT,
                CouponValueType.PERCENTAGE,
                BigDecimal.valueOf(15),
                CouponAppliesTo.SPECIFIC_PRODUCTS,
                LocalDate.parse("2026-04-07"),
                LocalTime.MIDNIGHT,
                UserId.generate(),
                Instant.parse("2026-04-07T12:00:00Z")
        ).reconfigure(
                "MERCH15",
                null,
                "MERCH15",
                CouponMethod.AUTOMATIC,
                CouponDiscountType.PRODUCT,
                CouponValueType.PERCENTAGE,
                BigDecimal.valueOf(15),
                CouponAppliesTo.SPECIFIC_PRODUCTS,
                "[{\"category\":\"MERCHANDISING\"}]",
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
                LocalDate.parse("2026-04-07"),
                LocalTime.MIDNIGHT,
                false,
                null,
                null,
                CouponStatus.ACTIVE,
                10,
                CouponTriggerType.NONE,
                null,
                null,
                false,
                "[]",
                "{}",
                Instant.parse("2026-04-07T12:00:00Z")
        );

        when(couponRepository.findAutomatic()).thenReturn(List.of(categoryCoupon));
        when(couponRedemptionRepository.countByCouponIdAndUserId(any(), any())).thenReturn(0L);

        Order repriced = service.applyRequestedPromotions(order, null, null);

        assertThat(repriced.getItems().get(0).getItemDiscountCouponId()).isEqualTo(categoryCoupon.getId());
        assertThat(repriced.getItems().get(0).getItemDiscountAmount().getAmount()).isEqualByComparingTo("30.00");
        assertThat(repriced.getSubtotal().getAmount()).isEqualByComparingTo("170.00");
        assertThat(repriced.getTotal().getAmount()).isEqualByComparingTo("270.00");
    }

    @Test
    void applyRequestedPromotionsShouldApplyTriggeredProductDiscount() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        CouponRepository couponRepository = mock(CouponRepository.class);
        CouponRedemptionRepository couponRedemptionRepository = mock(CouponRedemptionRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        OrderMapper orderMapper = new OrderMapper();
        TimeProvider timeProvider = fixedTimeProvider();

        OrderPromotionApplicationService service = new OrderPromotionApplicationService(
                orderRepository,
                couponRepository,
                couponRedemptionRepository,
                orderMapper,
                timeProvider,
                userRepository
        );

        Order order = sampleOrderWithTriggerAndTarget();
        when(userRepository.findById(order.getUserId())).thenReturn(Optional.of(sampleUser(order.getUserId(), "buyer@example.com")));
        OrderItem triggerItem = order.getItems().get(0);
        OrderItem targetItem = order.getItems().get(1);

        Coupon triggeredCoupon = Coupon.create(
                "IPA-FRIEND",
                null,
                "IPA-FRIEND",
                CouponMethod.AUTOMATIC,
                CouponDiscountType.PRODUCT,
                CouponValueType.PERCENTAGE,
                BigDecimal.valueOf(50),
                CouponAppliesTo.SPECIFIC_PRODUCTS,
                LocalDate.parse("2026-04-07"),
                LocalTime.MIDNIGHT,
                UserId.generate(),
                Instant.parse("2026-04-07T12:00:00Z")
        ).reconfigure(
                "IPA-FRIEND",
                null,
                "IPA-FRIEND",
                CouponMethod.AUTOMATIC,
                CouponDiscountType.PRODUCT,
                CouponValueType.PERCENTAGE,
                BigDecimal.valueOf(50),
                CouponAppliesTo.SPECIFIC_PRODUCTS,
                "[{\"productId\":\"" + targetItem.getProductId().getValue() + "\"}]",
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
                LocalDate.parse("2026-04-07"),
                LocalTime.MIDNIGHT,
                false,
                null,
                null,
                CouponStatus.ACTIVE,
                10,
                CouponTriggerType.PRODUCT_PURCHASE,
                triggerItem.getProductId().getValue().toString(),
                triggerItem.getProductName(),
                false,
                "[]",
                "{\"triggerQuantity\":1}",
                Instant.parse("2026-04-07T12:00:00Z")
        );

        when(couponRepository.findAutomatic()).thenReturn(List.of(triggeredCoupon));
        when(couponRedemptionRepository.countByCouponIdAndUserId(any(), any())).thenReturn(0L);

        Order repriced = service.applyRequestedPromotions(order, null, null);

        assertThat(repriced.getItems().get(0).getItemDiscountAmount().getAmount()).isEqualByComparingTo("0.00");
        assertThat(repriced.getItems().get(1).getItemDiscountCouponId()).isEqualTo(triggeredCoupon.getId());
        assertThat(repriced.getItems().get(1).getItemDiscountAmount().getAmount()).isEqualByComparingTo("100.00");
        assertThat(repriced.getSubtotal().getAmount()).isEqualByComparingTo("200.00");
        assertThat(repriced.getTotal().getAmount()).isEqualByComparingTo("300.00");
    }

    @Test
    void applyCouponShouldRejectPromotionChangesAfterPayment() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        CouponRepository couponRepository = mock(CouponRepository.class);
        CouponRedemptionRepository couponRedemptionRepository = mock(CouponRedemptionRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        OrderMapper orderMapper = new OrderMapper();
        TimeProvider timeProvider = fixedTimeProvider();

        OrderPromotionApplicationService service = new OrderPromotionApplicationService(
                orderRepository,
                couponRepository,
                couponRedemptionRepository,
                orderMapper,
                timeProvider,
                userRepository
        );

        Order paidOrder = sampleOrder().markAsPaid(timeProvider.now());
        when(orderRepository.findById(paidOrder.getId())).thenReturn(Optional.of(paidOrder));

        assertThatThrownBy(() -> service.applyCoupon(
                paidOrder.getId().getValue().toString(),
                "ORDER10",
                CouponDiscountType.ORDER
        ))
                .hasMessage("Promotions can only be changed while the order is awaiting payment");
    }

    @Test
    void repriceOrderShouldRejectPromotionChangesAfterPayment() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        CouponRepository couponRepository = mock(CouponRepository.class);
        CouponRedemptionRepository couponRedemptionRepository = mock(CouponRedemptionRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        OrderMapper orderMapper = new OrderMapper();
        TimeProvider timeProvider = fixedTimeProvider();

        OrderPromotionApplicationService service = new OrderPromotionApplicationService(
                orderRepository,
                couponRepository,
                couponRedemptionRepository,
                orderMapper,
                timeProvider,
                userRepository
        );

        Order paidOrder = sampleOrder().markAsPaid(timeProvider.now());
        when(orderRepository.findById(paidOrder.getId())).thenReturn(Optional.of(paidOrder));

        assertThatThrownBy(() -> service.repriceOrder(paidOrder.getId().getValue().toString()))
                .hasMessage("Promotions can only be changed while the order is awaiting payment");
    }

    @Test
    void applyRequestedPromotionsShouldRejectSpecificCustomerCouponForDifferentUser() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        CouponRepository couponRepository = mock(CouponRepository.class);
        CouponRedemptionRepository couponRedemptionRepository = mock(CouponRedemptionRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        OrderMapper orderMapper = new OrderMapper();
        TimeProvider timeProvider = fixedTimeProvider();

        OrderPromotionApplicationService service = new OrderPromotionApplicationService(
                orderRepository,
                couponRepository,
                couponRedemptionRepository,
                orderMapper,
                timeProvider,
                userRepository
        );

        Order order = sampleOrder();
        Coupon specificCoupon = Coupon.create(
                "DIST5ONA",
                "DIST5ONA",
                "DIST5ONA",
                CouponMethod.CODE,
                CouponDiscountType.ORDER,
                CouponValueType.PERCENTAGE,
                BigDecimal.valueOf(5),
                CouponAppliesTo.ENTIRE_ORDER,
                LocalDate.parse("2026-04-07"),
                LocalTime.MIDNIGHT,
                UserId.generate(),
                Instant.parse("2026-04-07T12:00:00Z")
        ).reconfigure(
                "DIST5ONA",
                "DIST5ONA",
                "DIST5ONA",
                CouponMethod.CODE,
                CouponDiscountType.ORDER,
                CouponValueType.PERCENTAGE,
                BigDecimal.valueOf(5),
                CouponAppliesTo.ENTIRE_ORDER,
                "[]",
                CouponCustomerEligibility.SPECIFIC_CUSTOMERS,
                null,
                null,
                false,
                null,
                false,
                null,
                false,
                false,
                false,
                LocalDate.parse("2026-04-07"),
                LocalTime.MIDNIGHT,
                false,
                null,
                null,
                CouponStatus.ACTIVE,
                10,
                CouponTriggerType.NONE,
                null,
                null,
                false,
                "[\"ona@mail.com\"]",
                "{}",
                Instant.parse("2026-04-07T12:00:00Z")
        );

        when(userRepository.findById(order.getUserId())).thenReturn(Optional.of(sampleUser(order.getUserId(), "other@mail.com")));
        when(couponRepository.findByCode("DIST5ONA")).thenReturn(Optional.of(specificCoupon));

        assertThatThrownBy(() -> service.applyRequestedPromotion(order, "DIST5ONA"))
                .hasMessage("Coupon is only available for specific customers");
    }

    private static TimeProvider fixedTimeProvider() {
        return new TimeProvider() {
            @Override
            public Instant now() {
                return Instant.parse("2026-04-07T12:00:00Z");
            }

            @Override
            public LocalDateTime nowDateTime() {
                return LocalDateTime.parse("2026-04-07T09:00:00");
            }

            @Override
            public LocalDate nowDate() {
                return LocalDate.parse("2026-04-07");
            }
        };
    }

    private static Order sampleOrder() {
        return sampleOrder(null, null);
    }

    private static Order sampleOrderWithTriggerAndTarget() {
        OrderItem triggerItem = OrderItem.create(
                OrderId.generate(),
                ProductId.generate(),
                null,
                "IPA Trigger",
                ProductType.BEER,
                BeerType.IPA,
                null,
                null,
                1,
                Money.of(100)
        );
        OrderItem targetItem = OrderItem.create(
                OrderId.generate(),
                ProductId.generate(),
                null,
                "Lager Target",
                ProductType.BEER,
                BeerType.LAGER,
                null,
                null,
                1,
                Money.of(200)
        );

        return Order.create(
                UserId.generate(),
                ShippingAddress.of(
                        "Street 123",
                        null,
                        "Cordoba",
                        "Cordoba",
                        "5000",
                        DeliveryZoneId.generate()
                ),
                List.of(triggerItem, targetItem),
                Money.of(100),
                null,
                Instant.parse("2026-04-07T12:00:00Z")
        );
    }

    private static Order sampleOrder(ProductCategory productCategory, ProductSubcategory productSubcategory) {
        ProductType productType = productCategory == ProductCategory.MERCHANDISING ? ProductType.MERCH : ProductType.BEER;
        String productName = productCategory == ProductCategory.MERCHANDISING ? "Remera" : "IPA";
        BeerType beerType = productType == ProductType.BEER ? BeerType.IPA : null;

        OrderItem item = OrderItem.create(
                OrderId.generate(),
                ProductId.generate(),
                null,
                productName,
                productType,
                beerType,
                productCategory,
                productSubcategory,
                2,
                Money.of(100)
        );

        return Order.create(
                UserId.generate(),
                ShippingAddress.of(
                        "Street 123",
                        null,
                        "Cordoba",
                        "Cordoba",
                        "5000",
                        DeliveryZoneId.generate()
                ),
                List.of(item),
                Money.of(100),
                null,
                Instant.parse("2026-04-07T12:00:00Z")
        );
    }

    private static Coupon sampleCoupon(
            String code,
            CouponDiscountType discountType,
            int priority,
            boolean combineWithShipping,
            boolean combineWithOrder
    ) {
        return Coupon.create(
                code,
                code,
                code,
                CouponMethod.CODE,
                discountType,
                discountType == CouponDiscountType.SHIPPING ? CouponValueType.FIXED : CouponValueType.PERCENTAGE,
                discountType == CouponDiscountType.SHIPPING ? BigDecimal.valueOf(50) : BigDecimal.TEN,
                CouponAppliesTo.ENTIRE_ORDER,
                LocalDate.parse("2026-04-07"),
                LocalTime.MIDNIGHT,
                UserId.generate(),
                Instant.parse("2026-04-07T12:00:00Z")
        ).reconfigure(
                code,
                code,
                code,
                CouponMethod.CODE,
                discountType,
                discountType == CouponDiscountType.SHIPPING ? CouponValueType.FIXED : CouponValueType.PERCENTAGE,
                discountType == CouponDiscountType.SHIPPING ? BigDecimal.valueOf(50) : BigDecimal.TEN,
                CouponAppliesTo.ENTIRE_ORDER,
                "[]",
                CouponCustomerEligibility.EVERYONE,
                null,
                null,
                false,
                null,
                false,
                null,
                false,
                combineWithOrder,
                combineWithShipping,
                LocalDate.parse("2026-04-07"),
                LocalTime.MIDNIGHT,
                false,
                null,
                null,
                CouponStatus.ACTIVE,
                priority,
                CouponTriggerType.NONE,
                null,
                null,
                false,
                "[]",
                "{}",
                Instant.parse("2026-04-07T12:00:00Z")
        );
    }

    private static Coupon sampleAutomaticCoupon(String code, CouponDiscountType discountType, int priority) {
        return Coupon.create(
                code,
                null,
                code,
                CouponMethod.AUTOMATIC,
                discountType,
                discountType == CouponDiscountType.SHIPPING ? CouponValueType.FIXED : CouponValueType.PERCENTAGE,
                discountType == CouponDiscountType.SHIPPING ? BigDecimal.valueOf(50) : BigDecimal.TEN,
                CouponAppliesTo.ENTIRE_ORDER,
                LocalDate.parse("2026-04-07"),
                LocalTime.MIDNIGHT,
                UserId.generate(),
                Instant.parse("2026-04-07T12:00:00Z")
        ).reconfigure(
                code,
                null,
                code,
                CouponMethod.AUTOMATIC,
                discountType,
                discountType == CouponDiscountType.SHIPPING ? CouponValueType.FIXED : CouponValueType.PERCENTAGE,
                discountType == CouponDiscountType.SHIPPING ? BigDecimal.valueOf(50) : BigDecimal.TEN,
                CouponAppliesTo.ENTIRE_ORDER,
                "[]",
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
                LocalDate.parse("2026-04-07"),
                LocalTime.MIDNIGHT,
                false,
                null,
                null,
                CouponStatus.ACTIVE,
                priority,
                CouponTriggerType.NONE,
                null,
                null,
                false,
                "[]",
                "{}",
                Instant.parse("2026-04-07T12:00:00Z")
        );
    }

    private static User sampleUser(UserId userId, String email) {
        return User.builder()
                .id(userId)
                .email(email)
                .passwordHash("hash")
                .firstName("Buyer")
                .lastName("User")
                .role(UserRole.USER)
                .isDistributor(false)
                .createdAt(Instant.parse("2026-04-07T12:00:00Z"))
                .updatedAt(Instant.parse("2026-04-07T12:00:00Z"))
                .build();
    }
}

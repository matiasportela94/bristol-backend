package com.bristol.domain.coupon;

import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderId;
import com.bristol.domain.order.OrderItem;
import com.bristol.domain.order.ProductType;
import com.bristol.domain.order.ShippingAddress;
import com.bristol.domain.product.BeerType;
import com.bristol.domain.product.ProductCategory;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductSubcategory;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.UserId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PromotionEngineTest {

    private final TimeProvider timeProvider = new TimeProvider() {
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

    private final PromotionEngine promotionEngine = new PromotionEngine(timeProvider);

    @Test
    void evaluateShouldKeepBothPromotionsWhenTheyCanCombine() {
        Order order = sampleOrder();
        Coupon orderCoupon = sampleCoupon("ORDER10", CouponDiscountType.ORDER, 10, true, false);
        Coupon shippingCoupon = sampleCoupon("SHIP50", CouponDiscountType.SHIPPING, 5, false, true);

        PromotionEvaluationResult result = promotionEngine.evaluate(order, List.of(orderCoupon, shippingCoupon));

        assertThat(result.getOrderPromotion()).isPresent();
        assertThat(result.getShippingPromotion()).isPresent();
        assertThat(result.getOrderPromotion().orElseThrow().discountAmount().getAmount()).isEqualByComparingTo("20.00");
        assertThat(result.getShippingPromotion().orElseThrow().discountAmount().getAmount()).isEqualByComparingTo("50.00");
    }

    @Test
    void evaluateShouldDropLowerPriorityPromotionWhenTheyCannotCombine() {
        Order order = sampleOrder();
        Coupon lowerPriorityOrderCoupon = sampleCoupon("ORDER10", CouponDiscountType.ORDER, 1, false, false);
        Coupon higherPriorityShippingCoupon = sampleCoupon("SHIP50", CouponDiscountType.SHIPPING, 10, false, false);

        PromotionEvaluationResult result = promotionEngine.evaluate(order, List.of(lowerPriorityOrderCoupon, higherPriorityShippingCoupon));

        assertThat(result.getOrderPromotion()).isEmpty();
        assertThat(result.getShippingPromotion()).isPresent();
        assertThat(result.containsCoupon(higherPriorityShippingCoupon.getId())).isTrue();
        assertThat(result.containsCoupon(lowerPriorityOrderCoupon.getId())).isFalse();
    }

    @Test
    void validateCouponShouldRejectUnsupportedOrderScope() {
        Order order = sampleOrder();
        Coupon scopedOrderCoupon = sampleCoupon("ORDER10", CouponDiscountType.ORDER, 0, true, false).reconfigure(
                "Scoped order",
                "ORDER10",
                "Scoped order",
                CouponMethod.CODE,
                CouponDiscountType.ORDER,
                CouponValueType.PERCENTAGE,
                BigDecimal.TEN,
                CouponAppliesTo.SPECIFIC_PRODUCTS,
                "[{\"productId\":\"abc-123\"}]",
                CouponCustomerEligibility.EVERYONE,
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
                0,
                CouponTriggerType.NONE,
                null,
                null,
                false,
                "[]",
                "{}",
                timeProvider.now()
        );

        assertThatThrownBy(() -> promotionEngine.validateCoupon(order, scopedOrderCoupon))
                .isInstanceOf(ValidationException.class)
                .hasMessage("This coupon scope is not yet supported for order repricing");
    }

    @Test
    void validateCouponShouldRejectPerCustomerLimitWhenRedemptionHistoryReached() {
        CouponRedemptionRepository couponRedemptionRepository = mock(CouponRedemptionRepository.class);
        PromotionEngine promotionEngineWithHistory = new PromotionEngine(timeProvider, couponRedemptionRepository);
        Order order = sampleOrder();
        Coupon limitedCoupon = sampleCoupon("LIMIT1", CouponDiscountType.ORDER, 10, true, true).reconfigure(
                "LIMIT1",
                "LIMIT1",
                "Limited",
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
                true,
                1,
                false,
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
                timeProvider.now()
        );

        when(couponRedemptionRepository.countByCouponIdAndUserId(limitedCoupon.getId(), order.getUserId())).thenReturn(1L);

        assertThatThrownBy(() -> promotionEngineWithHistory.validateCoupon(order, limitedCoupon))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Coupon has reached its per-customer usage limit");
    }

    @Test
    void validateCouponShouldRejectTotalUseLimitWhenRedemptionHistoryReached() {
        CouponRedemptionRepository couponRedemptionRepository = mock(CouponRedemptionRepository.class);
        PromotionEngine promotionEngineWithHistory = new PromotionEngine(timeProvider, couponRedemptionRepository);
        Order order = sampleOrder();
        Coupon limitedCoupon = sampleCoupon("TOTAL1", CouponDiscountType.ORDER, 10, true, true).reconfigure(
                "TOTAL1",
                "TOTAL1",
                "Limited total",
                CouponMethod.CODE,
                CouponDiscountType.ORDER,
                CouponValueType.PERCENTAGE,
                BigDecimal.TEN,
                CouponAppliesTo.ENTIRE_ORDER,
                "[]",
                CouponCustomerEligibility.EVERYONE,
                null,
                null,
                true,
                1,
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
                10,
                CouponTriggerType.NONE,
                null,
                null,
                false,
                "[]",
                "{}",
                timeProvider.now()
        );

        when(couponRedemptionRepository.countByCouponId(limitedCoupon.getId())).thenReturn(1L);

        assertThatThrownBy(() -> promotionEngineWithHistory.validateCoupon(order, limitedCoupon))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Coupon has reached its total usage limit");
    }

    @Test
    void evaluateShouldApplyProductPromotionToMatchingItems() {
        Order order = sampleOrder();
        Coupon productCoupon = sampleCoupon("IPA20", CouponDiscountType.PRODUCT, 10, true, true).reconfigure(
                "IPA20",
                "IPA20",
                "IPA selected",
                CouponMethod.CODE,
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
                timeProvider.now()
        );

        PromotionEvaluationResult result = promotionEngine.evaluate(order, List.of(productCoupon));

        assertThat(result.getProductPromotion()).isPresent();
        assertThat(result.repricedItems().get(0).getItemDiscountAmount().getAmount()).isEqualByComparingTo("40.00");
        assertThat(result.repricedItems().get(0).getSubtotal().getAmount()).isEqualByComparingTo("160.00");
    }

    @Test
    void evaluateShouldApplyProductPromotionByCategory() {
        Order order = sampleOrderWithProductSnapshot(ProductCategory.MERCHANDISING, ProductSubcategory.REMERA);
        Coupon categoryCoupon = sampleCoupon("MERCH15", CouponDiscountType.PRODUCT, 10, true, true).reconfigure(
                "MERCH15",
                "MERCH15",
                "Merch selected",
                CouponMethod.CODE,
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
                timeProvider.now()
        );

        PromotionEvaluationResult result = promotionEngine.evaluate(order, List.of(categoryCoupon));

        assertThat(result.getProductPromotion()).isPresent();
        assertThat(result.repricedItems().get(0).getItemDiscountAmount().getAmount()).isEqualByComparingTo("30.00");
        assertThat(result.repricedItems().get(0).getSubtotal().getAmount()).isEqualByComparingTo("170.00");
    }

    @Test
    void evaluateShouldApplyProductPromotionBySubcategory() {
        Order order = sampleOrderWithProductSnapshot(ProductCategory.MERCHANDISING, ProductSubcategory.REMERA);
        Coupon subcategoryCoupon = sampleCoupon("SHIRT25", CouponDiscountType.PRODUCT, 10, true, true).reconfigure(
                "SHIRT25",
                "SHIRT25",
                "Shirt selected",
                CouponMethod.CODE,
                CouponDiscountType.PRODUCT,
                CouponValueType.PERCENTAGE,
                BigDecimal.valueOf(25),
                CouponAppliesTo.SPECIFIC_PRODUCTS,
                "[{\"subcategory\":\"REMERA\"}]",
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
                timeProvider.now()
        );

        PromotionEvaluationResult result = promotionEngine.evaluate(order, List.of(subcategoryCoupon));

        assertThat(result.getProductPromotion()).isPresent();
        assertThat(result.repricedItems().get(0).getItemDiscountAmount().getAmount()).isEqualByComparingTo("50.00");
        assertThat(result.repricedItems().get(0).getSubtotal().getAmount()).isEqualByComparingTo("150.00");
    }

    @Test
    void evaluateShouldApplyBuyXGetYPromotionToMatchingItems() {
        Order order = sampleOrderWithQuantity(3);
        Coupon buyXGetYCoupon = sampleCoupon("THREEXTWO", CouponDiscountType.PRODUCT, 10, true, true).reconfigure(
                "THREEXTWO",
                "THREEXTWO",
                "3x2",
                CouponMethod.CODE,
                CouponDiscountType.PRODUCT,
                CouponValueType.FIXED,
                BigDecimal.ONE,
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
                CouponTriggerType.BUY_X_GET_Y,
                null,
                null,
                false,
                "[]",
                "{\"buyX\":2,\"getY\":1}",
                timeProvider.now()
        );

        PromotionEvaluationResult result = promotionEngine.evaluate(order, List.of(buyXGetYCoupon));

        assertThat(result.getProductPromotion()).isPresent();
        assertThat(result.getProductPromotion().orElseThrow().discountAmount().getAmount()).isEqualByComparingTo("100.00");
        assertThat(result.repricedItems().get(0).getSubtotal().getAmount()).isEqualByComparingTo("200.00");
    }

    @Test
    void evaluateShouldApplyPercentageOnQuantityWhenThresholdIsMet() {
        Order order = sampleOrderWithQuantity(3);
        Coupon quantityCoupon = sampleCoupon("QTY10", CouponDiscountType.PRODUCT, 10, true, true).reconfigure(
                "QTY10",
                "QTY10",
                "10 off on quantity",
                CouponMethod.CODE,
                CouponDiscountType.PRODUCT,
                CouponValueType.PERCENTAGE,
                BigDecimal.TEN,
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
                "{\"type\":\"percentage_on_quantity\",\"minQuantity\":3}",
                timeProvider.now()
        );

        PromotionEvaluationResult result = promotionEngine.evaluate(order, List.of(quantityCoupon));

        assertThat(result.getProductPromotion()).isPresent();
        assertThat(result.getProductPromotion().orElseThrow().discountAmount().getAmount()).isEqualByComparingTo("30.00");
        assertThat(result.repricedItems().get(0).getSubtotal().getAmount()).isEqualByComparingTo("270.00");
    }

    @Test
    void evaluateShouldApplyTriggeredProductPromotionWhenTriggerItemExists() {
        Order order = sampleOrderWithTriggerAndTarget();
        OrderItem triggerItem = order.getItems().get(0);
        OrderItem targetItem = order.getItems().get(1);

        Coupon triggeredCoupon = sampleCoupon("IPA-FRIEND", CouponDiscountType.PRODUCT, 10, true, true).reconfigure(
                "IPA-FRIEND",
                "IPA-FRIEND",
                "Trigger discount",
                CouponMethod.CODE,
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
                timeProvider.now()
        );

        PromotionEvaluationResult result = promotionEngine.evaluate(order, List.of(triggeredCoupon));

        assertThat(result.getProductPromotion()).isPresent();
        assertThat(result.repricedItems().get(0).getItemDiscountAmount().getAmount()).isEqualByComparingTo("0.00");
        assertThat(result.repricedItems().get(1).getItemDiscountAmount().getAmount()).isEqualByComparingTo("100.00");
        assertThat(result.repricedItems().get(1).getSubtotal().getAmount()).isEqualByComparingTo("100.00");
    }

    @Test
    void evaluateShouldApplyMultipleProductPromotionsToDifferentItemsEvenWhenNotMarkedCombinable() {
        Order order = sampleOrderWithBeerAndMerch();

        Coupon ipaCoupon = sampleCoupon("IPA3X2", CouponDiscountType.PRODUCT, 50, true, true).reconfigure(
                "IPA3X2",
                null,
                "3x2 IPA",
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
                LocalDate.parse("2026-04-07"),
                LocalTime.MIDNIGHT,
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
                timeProvider.now()
        );

        Coupon merchCoupon = sampleCoupon("MERCH10", CouponDiscountType.PRODUCT, 60, true, true).reconfigure(
                "MERCH10",
                null,
                "10% Merch",
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
                LocalDate.parse("2026-04-07"),
                LocalTime.MIDNIGHT,
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
                timeProvider.now()
        );

        PromotionEvaluationResult result = promotionEngine.evaluate(order, List.of(merchCoupon, ipaCoupon));

        assertThat(result.getProductPromotions()).hasSize(2);
        assertThat(result.repricedItems().get(0).getItemDiscountAmount().getAmount()).isEqualByComparingTo("100.00");
        assertThat(result.repricedItems().get(0).getSubtotal().getAmount()).isEqualByComparingTo("200.00");
        assertThat(result.repricedItems().get(1).getItemDiscountAmount().getAmount()).isEqualByComparingTo("20.00");
        assertThat(result.repricedItems().get(1).getSubtotal().getAmount()).isEqualByComparingTo("180.00");
    }

    private Order sampleOrder() {
        return sampleOrderWithProductSnapshot(null, null);
    }

    private Order sampleOrderWithTriggerAndTarget() {
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
                timeProvider.now()
        );
    }

    private Order sampleOrderWithQuantity(int quantity) {
        return sampleOrderWithSnapshot(quantity, null, null);
    }

    private Order sampleOrderWithProductSnapshot(
            ProductCategory productCategory,
            ProductSubcategory productSubcategory
    ) {
        return sampleOrderWithSnapshot(2, productCategory, productSubcategory);
    }

    private Order sampleOrderWithSnapshot(
            int quantity,
            ProductCategory productCategory,
            ProductSubcategory productSubcategory
    ) {
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
                quantity,
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
                timeProvider.now()
        );
    }

    private Order sampleOrderWithBeerAndMerch() {
        List<OrderItem> items = new ArrayList<>();
        items.add(OrderItem.create(
                OrderId.generate(),
                ProductId.generate(),
                null,
                "IPA",
                ProductType.BEER,
                BeerType.IPA,
                ProductCategory.PRODUCTOS,
                ProductSubcategory.CAN,
                3,
                Money.of(100)
        ));
        items.add(OrderItem.create(
                OrderId.generate(),
                ProductId.generate(),
                null,
                "Remera",
                ProductType.MERCH,
                null,
                ProductCategory.MERCHANDISING,
                ProductSubcategory.REMERA,
                1,
                Money.of(200)
        ));

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
                items,
                Money.of(100),
                null,
                timeProvider.now()
        );
    }

    private Coupon sampleCoupon(
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
                timeProvider.now()
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
                timeProvider.now()
        );
    }
}

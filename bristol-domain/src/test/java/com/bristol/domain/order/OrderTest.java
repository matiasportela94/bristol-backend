package com.bristol.domain.order;

import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.product.BeerType;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.UserId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    void createShouldRebindItemsToFinalOrderId() {
        OrderId placeholderOrderId = OrderId.generate();
        OrderItem item = OrderItem.create(
                placeholderOrderId,
                ProductId.generate(),
                null,
                "IPA",
                ProductType.BEER,
                BeerType.IPA,
                2,
                Money.of(100)
        );

        Order order = Order.create(
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
                Money.of(50),
                null,
                Instant.parse("2026-03-31T12:00:00Z")
        );

        assertThat(order.getId()).isNotEqualTo(placeholderOrderId);
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getItems().get(0).getOrderId()).isEqualTo(order.getId());
    }

    @Test
    void applyPromotionsShouldReplacePromotionStateAtomically() {
        OrderId placeholderOrderId = OrderId.generate();
        OrderItem item = OrderItem.create(
                placeholderOrderId,
                ProductId.generate(),
                null,
                "IPA",
                ProductType.BEER,
                BeerType.IPA,
                2,
                Money.of(100)
        );

        Order order = Order.create(
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
                Money.of(50),
                null,
                Instant.parse("2026-03-31T12:00:00Z")
        );

        Order repriced = order.applyPromotions(
                com.bristol.domain.coupon.CouponId.generate(),
                Money.of(20),
                null,
                Money.zero(),
                Instant.parse("2026-03-31T12:05:00Z")
        );

        assertThat(repriced.getOrderDiscountAmount().getAmount()).isEqualByComparingTo("20.00");
        assertThat(repriced.getShippingDiscountAmount().getAmount()).isEqualByComparingTo("0.00");
        assertThat(repriced.getTotal().getAmount()).isEqualByComparingTo("230.00");
    }

    @Test
    void applyPromotionsShouldRejectPaidOrders() {
        OrderId placeholderOrderId = OrderId.generate();
        OrderItem item = OrderItem.create(
                placeholderOrderId,
                ProductId.generate(),
                null,
                "IPA",
                ProductType.BEER,
                BeerType.IPA,
                2,
                Money.of(100)
        );

        Order paidOrder = Order.create(
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
                        Money.of(50),
                        null,
                        Instant.parse("2026-03-31T12:00:00Z")
                )
                .markAsPaid(Instant.parse("2026-03-31T12:10:00Z"));

        assertThatThrownBy(() -> paidOrder.applyPromotions(
                com.bristol.domain.coupon.CouponId.generate(),
                Money.of(20),
                null,
                Money.zero(),
                Instant.parse("2026-03-31T12:15:00Z")
        ))
                .hasMessage("Promotions can only be changed while the order is awaiting payment");
    }
}

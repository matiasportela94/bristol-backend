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
}

package com.bristol.infrastructure.persistence.repository;

import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderId;
import com.bristol.domain.order.OrderItem;
import com.bristol.domain.order.ProductType;
import com.bristol.domain.order.ShippingAddress;
import com.bristol.domain.product.BeerType;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.UserId;
import com.bristol.infrastructure.persistence.entity.OrderEntity;
import com.bristol.infrastructure.persistence.entity.OrderItemEntity;
import com.bristol.infrastructure.persistence.mapper.OrderItemMapper;
import com.bristol.infrastructure.persistence.mapper.OrderMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderRepositoryImplTest {

    @Test
    void saveShouldPersistOrderItemsAndReturnOrderWithItems() {
        JpaOrderRepository jpaOrderRepository = mock(JpaOrderRepository.class);
        JpaOrderItemRepository jpaOrderItemRepository = mock(JpaOrderItemRepository.class);
        OrderMapper orderMapper = mock(OrderMapper.class);
        OrderItemMapper orderItemMapper = mock(OrderItemMapper.class);
        OrderRepositoryImpl repository = new OrderRepositoryImpl(
                jpaOrderRepository,
                jpaOrderItemRepository,
                orderMapper,
                orderItemMapper
        );

        Order order = sampleOrder();
        Order orderWithoutItems = order.toBuilder().items(List.of()).build();
        OrderEntity orderEntity = sampleOrderEntity(order.getId().getValue());
        OrderItemEntity orderItemEntity = sampleOrderItemEntity(order.getId().getValue(), order.getItems().get(0).getId().getValue());

        when(orderMapper.toEntity(order)).thenReturn(orderEntity);
        when(jpaOrderRepository.save(orderEntity)).thenReturn(orderEntity);
        when(orderMapper.toDomain(orderEntity)).thenReturn(orderWithoutItems);
        when(orderItemMapper.toEntity(order.getItems().get(0))).thenReturn(orderItemEntity);

        Order saved = repository.save(order);

        verify(jpaOrderItemRepository).deleteByOrderId(order.getId().getValue());
        verify(jpaOrderItemRepository).saveAll(List.of(orderItemEntity));
        assertThat(saved.getItems()).containsExactlyElementsOf(order.getItems());
    }

    @Test
    void findByIdShouldAttachPersistedItems() {
        JpaOrderRepository jpaOrderRepository = mock(JpaOrderRepository.class);
        JpaOrderItemRepository jpaOrderItemRepository = mock(JpaOrderItemRepository.class);
        OrderMapper orderMapper = mock(OrderMapper.class);
        OrderItemMapper orderItemMapper = mock(OrderItemMapper.class);
        OrderRepositoryImpl repository = new OrderRepositoryImpl(
                jpaOrderRepository,
                jpaOrderItemRepository,
                orderMapper,
                orderItemMapper
        );

        Order order = sampleOrder();
        Order orderWithoutItems = order.toBuilder().items(List.of()).build();
        OrderEntity orderEntity = sampleOrderEntity(order.getId().getValue());
        OrderItemEntity orderItemEntity = sampleOrderItemEntity(order.getId().getValue(), order.getItems().get(0).getId().getValue());

        when(jpaOrderRepository.findById(order.getId().getValue())).thenReturn(Optional.of(orderEntity));
        when(orderMapper.toDomain(orderEntity)).thenReturn(orderWithoutItems);
        when(jpaOrderItemRepository.findByOrderId(order.getId().getValue())).thenReturn(List.of(orderItemEntity));
        when(orderItemMapper.toDomain(orderItemEntity)).thenReturn(order.getItems().get(0));

        Optional<Order> loaded = repository.findById(order.getId());

        assertThat(loaded).isPresent();
        assertThat(loaded.orElseThrow().getItems()).containsExactly(order.getItems().get(0));
    }

    private static Order sampleOrder() {
        Instant now = Instant.parse("2026-03-31T12:00:00Z");
        OrderItem item = OrderItem.create(
                OrderId.generate(),
                ProductId.generate(),
                null,
                "IPA",
                ProductType.BEER,
                BeerType.IPA,
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
                Money.of(50),
                null,
                now
        );
    }

    private static OrderEntity sampleOrderEntity(UUID orderId) {
        return OrderEntity.builder()
                .id(orderId)
                .userId(UUID.randomUUID())
                .orderStatus(OrderEntity.OrderStatusEnum.PENDING_PAYMENT)
                .orderDate(Instant.parse("2026-03-31T12:00:00Z"))
                .shippingAddressLine1("Street 123")
                .shippingCity("Cordoba")
                .shippingProvince("Cordoba")
                .deliveryZoneId(UUID.randomUUID())
                .subtotal(BigDecimal.valueOf(200))
                .orderDiscountAmount(BigDecimal.ZERO)
                .shippingCost(BigDecimal.valueOf(50))
                .shippingDiscountAmount(BigDecimal.ZERO)
                .total(BigDecimal.valueOf(250))
                .stockUpdated(false)
                .createdAt(Instant.parse("2026-03-31T12:00:00Z"))
                .updatedAt(Instant.parse("2026-03-31T12:00:00Z"))
                .build();
    }

    private static OrderItemEntity sampleOrderItemEntity(UUID orderId, UUID orderItemId) {
        return OrderItemEntity.builder()
                .id(orderItemId)
                .orderId(orderId)
                .productId(UUID.randomUUID())
                .productName("IPA")
                .productType(OrderItemEntity.ProductTypeEnum.BEER)
                .beerType(OrderItemEntity.BeerTypeEnum.IPA)
                .quantity(2)
                .pricePerUnit(BigDecimal.valueOf(100))
                .itemDiscountAmount(BigDecimal.ZERO)
                .subtotal(BigDecimal.valueOf(200))
                .build();
    }
}


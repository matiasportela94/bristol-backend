package com.bristol.application.order.usecase;

import com.bristol.application.delivery.service.DeliverySchedulingService;
import com.bristol.application.order.dto.OrderDto;
import com.bristol.application.order.service.StockManagementService;
import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderId;
import com.bristol.domain.order.OrderItem;
import com.bristol.domain.order.OrderRepository;
import com.bristol.domain.order.ProductType;
import com.bristol.domain.order.ShippingAddress;
import com.bristol.domain.product.BeerType;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.UserId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CancelOrderUseCaseTest {

    @Test
    void executeShouldClearCouponsAndCancelDelivery() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        StockManagementService stockManagementService = mock(StockManagementService.class);
        CouponRedemptionApplicationService couponRedemptionApplicationService = mock(CouponRedemptionApplicationService.class);
        DeliverySchedulingService deliverySchedulingService = mock(DeliverySchedulingService.class);
        OrderMapper orderMapper = mock(OrderMapper.class);
        CancelOrderUseCase useCase = new CancelOrderUseCase(
                orderRepository,
                stockManagementService,
                couponRedemptionApplicationService,
                deliverySchedulingService,
                orderMapper,
                fixedTimeProvider()
        );

        Instant now = Instant.parse("2026-04-13T12:00:00Z");
        Order order = sampleOrder().markStockAsUpdated(now).markAsPaid(now);
        Order cancelledOrder = order.cancel(now);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        doNothing().when(stockManagementService).restoreStockForOrder(order);
        when(orderRepository.save(any(Order.class))).thenReturn(cancelledOrder);
        when(orderMapper.toDto(cancelledOrder)).thenReturn(new OrderDto());

        useCase.execute(order.getId().getValue().toString());

        verify(stockManagementService).restoreStockForOrder(order);
        verify(couponRedemptionApplicationService).clearOrderRedemptions(any(Order.class), any(Instant.class));
        verify(deliverySchedulingService).cancelScheduledDelivery(any(Order.class));
    }

    private static TimeProvider fixedTimeProvider() {
        Instant fixedInstant = Instant.parse("2026-04-13T12:00:00Z");
        return new TimeProvider() {
            @Override
            public Instant now() {
                return fixedInstant;
            }

            @Override
            public LocalDateTime nowDateTime() {
                return LocalDateTime.of(2026, 4, 13, 9, 0);
            }

            @Override
            public LocalDate nowDate() {
                return LocalDate.of(2026, 4, 13);
            }
        };
    }

    private static Order sampleOrder() {
        OrderItem item = OrderItem.create(
                OrderId.generate(),
                ProductId.generate(),
                null,
                "IPA",
                ProductType.BEER,
                BeerType.IPA,
                1,
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
                Instant.parse("2026-04-13T12:00:00Z")
        );
    }
}

package com.bristol.application.order.usecase;

import com.bristol.application.order.dto.OrderDto;
import com.bristol.application.order.dto.UpdateOrderStatusRequest;
import com.bristol.application.order.service.StockManagementService;
import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderId;
import com.bristol.domain.order.OrderItem;
import com.bristol.domain.order.OrderRepository;
import com.bristol.domain.order.OrderStatus;
import com.bristol.domain.order.ProductType;
import com.bristol.domain.order.ShippingAddress;
import com.bristol.domain.product.BeerType;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.UserId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateOrderStatusUseCaseTest {

    @Test
    void executeShouldRecordCouponRedemptionsWhenOrderBecomesPaid() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        StockManagementService stockManagementService = mock(StockManagementService.class);
        CouponRedemptionApplicationService couponRedemptionApplicationService = mock(CouponRedemptionApplicationService.class);
        OrderMapper orderMapper = mock(OrderMapper.class);
        UpdateOrderStatusUseCase useCase = new UpdateOrderStatusUseCase(
                orderRepository,
                stockManagementService,
                couponRedemptionApplicationService,
                orderMapper
        );

        Order order = sampleOrder();
        Order paidOrder = order.markAsPaid(Instant.now()).markStockAsUpdated(Instant.now());

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        doNothing().when(stockManagementService).deductStockForOrder(any(Order.class));
        when(orderRepository.save(any(Order.class))).thenReturn(paidOrder);
        when(orderMapper.toDto(paidOrder)).thenReturn(new OrderDto());

        useCase.execute(order.getId().getValue().toString(), new UpdateOrderStatusRequest(OrderStatus.PAID));

        verify(couponRedemptionApplicationService).recordPaidOrderRedemptions(any(Order.class), any(Instant.class));
    }

    @Test
    void executeShouldClearCouponRedemptionsWhenOrderIsCancelled() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        StockManagementService stockManagementService = mock(StockManagementService.class);
        CouponRedemptionApplicationService couponRedemptionApplicationService = mock(CouponRedemptionApplicationService.class);
        OrderMapper orderMapper = mock(OrderMapper.class);
        UpdateOrderStatusUseCase useCase = new UpdateOrderStatusUseCase(
                orderRepository,
                stockManagementService,
                couponRedemptionApplicationService,
                orderMapper
        );

        Instant now = Instant.parse("2026-04-07T12:00:00Z");
        Order order = sampleOrder().markAsPaid(now).markStockAsUpdated(now);
        Order cancelledOrder = order.cancel(now);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        doNothing().when(stockManagementService).restoreStockForOrder(order);
        when(orderRepository.save(any(Order.class))).thenReturn(cancelledOrder);
        when(orderMapper.toDto(cancelledOrder)).thenReturn(new OrderDto());

        useCase.execute(order.getId().getValue().toString(), new UpdateOrderStatusRequest(OrderStatus.CANCELLED));

        verify(couponRedemptionApplicationService).clearOrderRedemptions(any(Order.class), any(Instant.class));
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
                Instant.parse("2026-04-07T12:00:00Z")
        );
    }
}

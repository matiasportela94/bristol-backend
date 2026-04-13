package com.bristol.application.payment.usecase;

import com.bristol.application.delivery.service.DeliverySchedulingService;
import com.bristol.application.order.service.StockManagementService;
import com.bristol.application.order.usecase.CouponRedemptionApplicationService;
import com.bristol.application.payment.dto.PaymentDto;
import com.bristol.application.payment.dto.RejectPaymentRequest;
import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderId;
import com.bristol.domain.order.OrderItem;
import com.bristol.domain.order.OrderRepository;
import com.bristol.domain.order.ProductType;
import com.bristol.domain.order.ShippingAddress;
import com.bristol.domain.payment.Payment;
import com.bristol.domain.payment.PaymentProvider;
import com.bristol.domain.payment.PaymentRepository;
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

class RejectPaymentUseCaseTest {

    @Test
    void executeShouldRejectPaymentAndMarkOrderAsFailed() {
        PaymentRepository paymentRepository = mock(PaymentRepository.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        StockManagementService stockManagementService = mock(StockManagementService.class);
        CouponRedemptionApplicationService couponRedemptionApplicationService = mock(CouponRedemptionApplicationService.class);
        DeliverySchedulingService deliverySchedulingService = mock(DeliverySchedulingService.class);
        PaymentMapper paymentMapper = mock(PaymentMapper.class);
        TimeProvider timeProvider = fixedTimeProvider();
        RejectPaymentUseCase useCase = new RejectPaymentUseCase(
                paymentRepository,
                orderRepository,
                stockManagementService,
                couponRedemptionApplicationService,
                deliverySchedulingService,
                paymentMapper,
                timeProvider
        );

        Instant now = Instant.parse("2026-04-13T12:00:00Z");
        Order order = sampleOrder().markStockAsUpdated(now).markPaymentInProcess(now);
        Order failedOrder = order.markPaymentFailed(now);
        Payment payment = samplePayment(order, now);
        Payment rejectedPayment = payment.reject("declined", now).toBuilder().paymentNumber(1000L).build();

        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.of(payment));
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        doNothing().when(stockManagementService).restoreStockForOrder(order);
        when(orderRepository.save(any(Order.class))).thenReturn(failedOrder);
        when(paymentRepository.save(any(Payment.class))).thenReturn(rejectedPayment);
        when(paymentMapper.toDto(rejectedPayment)).thenReturn(new PaymentDto());

        useCase.execute(payment.getId().getValue().toString(), new RejectPaymentRequest("declined"));

        verify(stockManagementService).restoreStockForOrder(order);
        verify(couponRedemptionApplicationService).clearOrderRedemptions(any(Order.class), any(Instant.class));
        verify(deliverySchedulingService).cancelScheduledDelivery(any(Order.class));
        verify(paymentRepository).save(any(Payment.class));
    }

    private static Payment samplePayment(Order order, Instant now) {
        return Payment.create(
                order.getId(),
                order.getUserId(),
                PaymentProvider.MANUAL,
                null,
                order.getFinalTotal(),
                "ARS",
                now
        );
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

    private static TimeProvider fixedTimeProvider() {
        return new TimeProvider() {
            @Override
            public Instant now() {
                return Instant.parse("2026-04-13T12:00:00Z");
            }

            @Override
            public LocalDateTime nowDateTime() {
                return LocalDateTime.parse("2026-04-13T09:00:00");
            }

            @Override
            public LocalDate nowDate() {
                return LocalDate.parse("2026-04-13");
            }
        };
    }
}

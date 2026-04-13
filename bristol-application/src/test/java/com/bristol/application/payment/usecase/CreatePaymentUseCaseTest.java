package com.bristol.application.payment.usecase;

import com.bristol.application.payment.dto.CreatePaymentRequest;
import com.bristol.application.payment.dto.PaymentDto;
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
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreatePaymentUseCaseTest {

    @Test
    void executeShouldCreatePaymentAndMoveOrderToPaymentInProcess() {
        PaymentRepository paymentRepository = mock(PaymentRepository.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        PaymentMapper paymentMapper = mock(PaymentMapper.class);
        TimeProvider timeProvider = fixedTimeProvider();
        CreatePaymentUseCase useCase = new CreatePaymentUseCase(
                paymentRepository,
                orderRepository,
                paymentMapper,
                timeProvider
        );

        Instant now = Instant.parse("2026-04-13T12:00:00Z");
        Order order = sampleOrder();
        Payment payment = Payment.create(
                order.getId(),
                order.getUserId(),
                PaymentProvider.MANUAL,
                null,
                order.getFinalTotal(),
                "ARS",
                now
        ).toBuilder().paymentNumber(1000L).build();

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(paymentRepository.findApprovedByOrderId(order.getId())).thenReturn(Optional.empty());
        when(orderRepository.save(any(Order.class))).thenReturn(order.markPaymentInProcess(now));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(paymentMapper.toDto(payment)).thenReturn(new PaymentDto());

        useCase.execute(CreatePaymentRequest.builder()
                .orderId(order.getId().getValue().toString())
                .provider(PaymentProvider.MANUAL)
                .build());

        verify(orderRepository).save(argThat(saved -> saved.getStatus() == com.bristol.domain.order.OrderStatus.PAYMENT_IN_PROCESS));
        verify(paymentRepository).save(argThat(saved -> "ARS".equals(saved.getCurrency())));
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

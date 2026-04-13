package com.bristol.application.payment.usecase;

import com.bristol.application.delivery.service.DeliverySchedulingService;
import com.bristol.application.order.usecase.CouponRedemptionApplicationService;
import com.bristol.application.payment.dto.ApprovePaymentRequest;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApprovePaymentUseCaseTest {

    @Test
    void executeShouldApprovePaymentMarkOrderPaidAndScheduleDelivery() {
        PaymentRepository paymentRepository = mock(PaymentRepository.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        CouponRedemptionApplicationService couponRedemptionApplicationService = mock(CouponRedemptionApplicationService.class);
        DeliverySchedulingService deliverySchedulingService = mock(DeliverySchedulingService.class);
        PaymentMapper paymentMapper = mock(PaymentMapper.class);
        TimeProvider timeProvider = fixedTimeProvider();
        ApprovePaymentUseCase useCase = new ApprovePaymentUseCase(
                paymentRepository,
                orderRepository,
                couponRedemptionApplicationService,
                deliverySchedulingService,
                paymentMapper,
                timeProvider
        );

        Instant now = Instant.parse("2026-04-13T12:00:00Z");
        Order order = sampleOrder().markPaymentInProcess(now);
        Order paidOrder = order.markAsPaid(now);
        Payment payment = samplePayment(order, now);
        Payment approvedPayment = payment.approve("mp-123", now).toBuilder().paymentNumber(1000L).build();

        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.of(payment));
        when(paymentRepository.findApprovedByOrderId(order.getId())).thenReturn(Optional.empty());
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(paidOrder);
        when(paymentRepository.save(any(Payment.class))).thenReturn(approvedPayment);
        when(paymentMapper.toDto(approvedPayment)).thenReturn(new PaymentDto());

        useCase.execute(payment.getId().getValue().toString(), new ApprovePaymentRequest("mp-123"));

        verify(orderRepository).save(any(Order.class));
        verify(couponRedemptionApplicationService).recordPaidOrderRedemptions(any(Order.class), any(Instant.class));
        verify(deliverySchedulingService).ensureScheduledForPaidOrder(any(Order.class));
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

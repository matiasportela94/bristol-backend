package com.bristol.application.delivery.service;

import com.bristol.domain.delivery.Delivery;
import com.bristol.domain.delivery.DeliveryCalendar;
import com.bristol.domain.delivery.DeliveryCalendarId;
import com.bristol.domain.delivery.DeliveryCalendarRepository;
import com.bristol.domain.delivery.DeliveryRepository;
import com.bristol.domain.delivery.DeliveryZoneType;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderId;
import com.bristol.domain.order.OrderItem;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeliverySchedulingServiceTest {

    @Test
    void ensureScheduledForPaidOrderShouldAssignNorthOrdersToNextMonday() {
        DeliveryRepository deliveryRepository = mock(DeliveryRepository.class);
        DeliveryCalendarRepository deliveryCalendarRepository = mock(DeliveryCalendarRepository.class);
        DeliverySchedulingService service = new DeliverySchedulingService(
                deliveryRepository,
                deliveryCalendarRepository,
                fixedTimeProvider()
        );

        Order order = samplePaidOrder(DeliveryZoneType.NORTE);

        when(deliveryRepository.findByOrderId(order.getId())).thenReturn(Optional.empty());
        when(deliveryCalendarRepository.findByZoneAndDate(
                eq(DeliveryZoneType.NORTE.getDeliveryZoneId()),
                eq(LocalDate.parse("2026-04-20"))
        )).thenReturn(Optional.empty());
        when(deliveryCalendarRepository.save(any(DeliveryCalendar.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(deliveryRepository.save(any(Delivery.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Delivery delivery = service.ensureScheduledForPaidOrder(order);

        assertEquals(LocalDate.parse("2026-04-20"), delivery.getScheduledDate());
        verify(deliveryCalendarRepository).findByZoneAndDate(
                DeliveryZoneType.NORTE.getDeliveryZoneId(),
                LocalDate.parse("2026-04-20")
        );
    }

    @Test
    void ensureScheduledForPaidOrderShouldMoveToFollowingWeekWhenNextRouteDayIsFull() {
        DeliveryRepository deliveryRepository = mock(DeliveryRepository.class);
        DeliveryCalendarRepository deliveryCalendarRepository = mock(DeliveryCalendarRepository.class);
        DeliverySchedulingService service = new DeliverySchedulingService(
                deliveryRepository,
                deliveryCalendarRepository,
                fixedTimeProvider()
        );

        Order order = samplePaidOrder(DeliveryZoneType.NORTE);
        Instant now = Instant.parse("2026-04-13T12:00:00Z");

        DeliveryCalendar fullCalendar = DeliveryCalendar.create(
                DeliveryZoneType.NORTE.getDeliveryZoneId(),
                LocalDate.parse("2026-04-20"),
                1,
                now
        ).bookSlot(now);

        when(deliveryRepository.findByOrderId(order.getId())).thenReturn(Optional.empty());
        when(deliveryCalendarRepository.findByZoneAndDate(
                eq(DeliveryZoneType.NORTE.getDeliveryZoneId()),
                eq(LocalDate.parse("2026-04-20"))
        )).thenReturn(Optional.of(fullCalendar));
        when(deliveryCalendarRepository.findByZoneAndDate(
                eq(DeliveryZoneType.NORTE.getDeliveryZoneId()),
                eq(LocalDate.parse("2026-04-27"))
        )).thenReturn(Optional.empty());
        when(deliveryCalendarRepository.save(any(DeliveryCalendar.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(deliveryRepository.save(any(Delivery.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Delivery delivery = service.ensureScheduledForPaidOrder(order);

        assertEquals(LocalDate.parse("2026-04-27"), delivery.getScheduledDate());
    }

    @Test
    void rescheduleDeliveryShouldAllowManualOverrideOutsideRouteDay() {
        DeliveryRepository deliveryRepository = mock(DeliveryRepository.class);
        DeliveryCalendarRepository deliveryCalendarRepository = mock(DeliveryCalendarRepository.class);
        DeliverySchedulingService service = new DeliverySchedulingService(
                deliveryRepository,
                deliveryCalendarRepository,
                fixedTimeProvider()
        );

        Order order = samplePaidOrder(DeliveryZoneType.NORTE);
        Instant now = Instant.parse("2026-04-13T12:00:00Z");

        DeliveryCalendar existingCalendar = DeliveryCalendar.create(
                DeliveryZoneType.NORTE.getDeliveryZoneId(),
                LocalDate.parse("2026-04-20"),
                100,
                now
        ).bookSlot(now);

        Delivery existingDelivery = Delivery.schedule(
                order.getId(),
                existingCalendar.getId(),
                LocalDate.parse("2026-04-20"),
                "Initial schedule",
                now
        );

        when(deliveryCalendarRepository.findById(existingCalendar.getId())).thenReturn(Optional.of(existingCalendar));
        when(deliveryCalendarRepository.findByZoneAndDate(
                eq(DeliveryZoneType.NORTE.getDeliveryZoneId()),
                eq(LocalDate.parse("2026-04-16"))
        )).thenReturn(Optional.empty());
        when(deliveryCalendarRepository.save(any(DeliveryCalendar.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(deliveryRepository.save(any(Delivery.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Delivery delivery = service.rescheduleDelivery(
                existingDelivery,
                order,
                LocalDate.parse("2026-04-16"),
                "Urgencia"
        );

        assertEquals(LocalDate.parse("2026-04-16"), delivery.getScheduledDate());
        verify(deliveryCalendarRepository).findById(existingCalendar.getId());
    }

    private static Order samplePaidOrder(DeliveryZoneType zoneType) {
        Instant now = Instant.parse("2026-04-13T12:00:00Z");
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
                        zoneType.getDeliveryZoneId()
                ),
                List.of(item),
                Money.of(100),
                null,
                now
        ).markAsPaid(now);
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

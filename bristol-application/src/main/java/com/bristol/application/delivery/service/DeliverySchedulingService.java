package com.bristol.application.delivery.service;

import com.bristol.domain.delivery.Delivery;
import com.bristol.domain.delivery.DeliveryCalendar;
import com.bristol.domain.delivery.DeliveryCalendarRepository;
import com.bristol.domain.delivery.DeliveryRepository;
import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.delivery.DeliveryZoneType;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderStatus;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Service
@RequiredArgsConstructor
public class DeliverySchedulingService {

    private static final int DEFAULT_CAPACITY = 100;
    private static final int MAX_ROUTE_WEEKS_TO_SCAN = 52;

    private final DeliveryRepository deliveryRepository;
    private final DeliveryCalendarRepository deliveryCalendarRepository;
    private final TimeProvider timeProvider;

    @Transactional
    public Delivery ensureScheduledForPaidOrder(Order order) {
        if (order.getStatus() != OrderStatus.PAID
                && order.getStatus() != OrderStatus.PROCESSING
                && order.getStatus() != OrderStatus.SHIPPED
                && order.getStatus() != OrderStatus.DELIVERED) {
            throw new ValidationException("Delivery can only be scheduled for paid orders");
        }

        return deliveryRepository.findByOrderId(order.getId())
                .orElseGet(() -> scheduleNewDelivery(order));
    }

    @Transactional
    public void cancelScheduledDelivery(Order order) {
        deliveryRepository.findByOrderId(order.getId()).ifPresent(delivery -> {
            releaseCalendarSlot(delivery);

            deliveryRepository.delete(delivery.getId());
        });
    }

    @Transactional
    public Delivery rescheduleDelivery(Delivery delivery, Order order, LocalDate requestedDate, String reason) {
        if (requestedDate == null) {
            throw new ValidationException("Scheduled date is required");
        }

        releaseCalendarSlot(delivery);

        DeliveryCalendar bookedCalendar = bookSpecificDate(
                order.getShippingAddress().getDeliveryZoneId(),
                requestedDate
        );

        Delivery rescheduledDelivery = delivery.reschedule(
                bookedCalendar.getId(),
                bookedCalendar.getDeliveryDate(),
                reason != null && !reason.isBlank() ? reason : "Manual admin update",
                timeProvider.now()
        );

        return deliveryRepository.save(rescheduledDelivery);
    }

    private Delivery scheduleNewDelivery(Order order) {
        DeliveryCalendar bookedCalendar = bookNextRouteDate(order.getShippingAddress().getDeliveryZoneId());

        Delivery delivery = Delivery.schedule(
                order.getId(),
                bookedCalendar.getId(),
                bookedCalendar.getDeliveryDate(),
                order.getNotes(),
                timeProvider.now()
        );

        return deliveryRepository.save(delivery);
    }

    private DeliveryCalendar bookNextRouteDate(DeliveryZoneId zoneId) {
        LocalDate nextCandidateDate = timeProvider.nowDate().with(
                TemporalAdjusters.next(routeDayForZone(zoneId))
        );

        for (int weekOffset = 0; weekOffset < MAX_ROUTE_WEEKS_TO_SCAN; weekOffset++) {
            LocalDate candidateDate = nextCandidateDate.plusWeeks(weekOffset);
            DeliveryCalendar calendar = findOrCreateCalendar(zoneId, candidateDate);

            if (calendar.hasAvailableCapacity()) {
                return deliveryCalendarRepository.save(calendar.bookSlot(timeProvider.now()));
            }
        }

        throw new ValidationException("No delivery capacity available for zone: " + zoneId.getValue());
    }

    private DeliveryCalendar bookSpecificDate(DeliveryZoneId zoneId, LocalDate date) {
        DeliveryCalendar calendar = findOrCreateCalendar(zoneId, date);

        if (!calendar.hasAvailableCapacity()) {
            throw new ValidationException("No delivery capacity available for " + date);
        }

        return deliveryCalendarRepository.save(calendar.bookSlot(timeProvider.now()));
    }

    private DeliveryCalendar findOrCreateCalendar(DeliveryZoneId zoneId, LocalDate date) {
        return deliveryCalendarRepository.findByZoneAndDate(zoneId, date)
                .orElseGet(() -> deliveryCalendarRepository.save(
                        DeliveryCalendar.create(zoneId, date, DEFAULT_CAPACITY, timeProvider.now())
                ));
    }

    private void releaseCalendarSlot(Delivery delivery) {
        deliveryCalendarRepository.findById(delivery.getDeliveryCalendarId())
                .filter(calendar -> calendar.getCurrentBookings() > 0)
                .map(calendar -> calendar.releaseSlot(timeProvider.now()))
                .ifPresent(deliveryCalendarRepository::save);
    }

    private DayOfWeek routeDayForZone(DeliveryZoneId zoneId) {
        if (DeliveryZoneType.NORTE.getDeliveryZoneId().equals(zoneId)) {
            return DayOfWeek.MONDAY;
        }
        if (DeliveryZoneType.CENTRO.getDeliveryZoneId().equals(zoneId)) {
            return DayOfWeek.TUESDAY;
        }
        if (DeliveryZoneType.SUR.getDeliveryZoneId().equals(zoneId)) {
            return DayOfWeek.WEDNESDAY;
        }

        throw new ValidationException("Unsupported delivery zone: " + zoneId.getValue());
    }
}

package com.bristol.domain.delivery;

import com.bristol.domain.order.OrderId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DeliveryTest {

    @Test
    void completeShouldAllowScheduledDeliveryAndKeepScheduledDate() {
        Instant now = Instant.parse("2026-04-16T10:15:30Z");
        LocalDate scheduledDate = LocalDate.of(2026, 4, 20);
        LocalDate actualDeliveryDate = LocalDate.of(2026, 4, 22);

        Delivery delivery = Delivery.schedule(
                new OrderId(UUID.randomUUID()),
                new DeliveryCalendarId(UUID.randomUUID()),
                scheduledDate,
                "Customer notes",
                now
        );

        Delivery completed = delivery.complete(actualDeliveryDate, "Entregado", now.plusSeconds(60));

        assertThat(completed.getStatus()).isEqualTo(DeliveryStatus.DELIVERED);
        assertThat(completed.getScheduledDate()).isEqualTo(scheduledDate);
        assertThat(completed.getActualDeliveryDate()).isEqualTo(actualDeliveryDate);
    }
}

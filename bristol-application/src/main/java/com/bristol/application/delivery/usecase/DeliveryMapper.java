package com.bristol.application.delivery.usecase;

import com.bristol.application.delivery.dto.DeliveryDto;
import com.bristol.domain.delivery.Delivery;
import org.springframework.stereotype.Component;

@Component
public class DeliveryMapper {
    public DeliveryDto toDto(Delivery delivery) {
        return DeliveryDto.builder()
                .id(delivery.getId().getValue().toString())
                .orderId(delivery.getOrderId().getValue().toString())
                .distributorId(null)
                .deliveryZoneId(delivery.getDeliveryCalendarId() != null ?
                        delivery.getDeliveryCalendarId().getValue().toString() : null)
                .address(null)
                .status(delivery.getStatus())
                .scheduledAt(delivery.getScheduledDate() != null ?
                        delivery.getScheduledDate().atStartOfDay().toInstant(java.time.ZoneOffset.UTC) : null)
                .deliveredAt(delivery.getActualDeliveryDate() != null ?
                        delivery.getActualDeliveryDate().atStartOfDay().toInstant(java.time.ZoneOffset.UTC) : null)
                .notes(delivery.getCustomerNotes())
                .createdAt(delivery.getCreatedAt())
                .updatedAt(delivery.getUpdatedAt())
                .build();
    }
}

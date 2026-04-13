package com.bristol.application.delivery.usecase;

import com.bristol.application.delivery.dto.DeliveryDto;
import com.bristol.domain.delivery.Delivery;
import com.bristol.domain.order.Order;
import com.bristol.domain.user.User;
import org.springframework.stereotype.Component;

@Component
public class DeliveryMapper {
    public DeliveryDto toDto(Delivery delivery) {
        return toDto(delivery, null, null);
    }

    public DeliveryDto toDto(Delivery delivery, Order order, User user) {
        return DeliveryDto.builder()
                .id(delivery.getId().getValue().toString())
                .deliveryNumber(delivery.getDeliveryNumber())
                .orderId(delivery.getOrderId().getValue().toString())
                .orderNumber(order != null ? order.getOrderNumber() : null)
                .userEmail(user != null ? user.getEmail() : null)
                .distributorId(null)
                .deliveryZoneId(order != null && order.getShippingAddress() != null
                        && order.getShippingAddress().getDeliveryZoneId() != null
                        ? order.getShippingAddress().getDeliveryZoneId().getValue().toString()
                        : null)
                .address(order != null && order.getShippingAddress() != null
                        ? order.getShippingAddress().getFullAddress()
                        : null)
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

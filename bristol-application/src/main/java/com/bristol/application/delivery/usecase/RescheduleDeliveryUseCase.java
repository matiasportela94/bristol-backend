package com.bristol.application.delivery.usecase;

import com.bristol.application.delivery.dto.DeliveryDto;
import com.bristol.application.delivery.dto.RescheduleDeliveryRequest;
import com.bristol.application.delivery.service.DeliverySchedulingService;
import com.bristol.domain.delivery.Delivery;
import com.bristol.domain.delivery.DeliveryId;
import com.bristol.domain.delivery.DeliveryRepository;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RescheduleDeliveryUseCase {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final DeliverySchedulingService deliverySchedulingService;
    private final DeliveryDtoAssembler deliveryDtoAssembler;

    @Transactional
    public DeliveryDto execute(String id, RescheduleDeliveryRequest request) {
        DeliveryId deliveryId = new DeliveryId(UUID.fromString(id));
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NotFoundException("Delivery", id));

        Order order = orderRepository.findById(delivery.getOrderId())
                .orElseThrow(() -> new NotFoundException("Order", delivery.getOrderId().getValue().toString()));

        Delivery rescheduledDelivery = deliverySchedulingService.rescheduleDelivery(
                delivery,
                order,
                request.getScheduledDate(),
                request.getReason()
        );

        return deliveryDtoAssembler.toDto(rescheduledDelivery);
    }
}

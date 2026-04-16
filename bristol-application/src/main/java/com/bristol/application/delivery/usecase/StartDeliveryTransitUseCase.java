package com.bristol.application.delivery.usecase;

import com.bristol.application.delivery.dto.DeliveryDto;
import com.bristol.application.delivery.dto.StartTransitRequest;
import com.bristol.domain.delivery.Delivery;
import com.bristol.domain.delivery.DeliveryId;
import com.bristol.domain.delivery.DeliveryRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import com.bristol.domain.shared.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use case to start delivery transit.
 */
@Service
@RequiredArgsConstructor
public class StartDeliveryTransitUseCase {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryDtoAssembler deliveryDtoAssembler;
    private final TimeProvider timeProvider;

    @Transactional
    public DeliveryDto execute(String id, StartTransitRequest request) {
        DeliveryId deliveryId = new DeliveryId(UUID.fromString(id));
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NotFoundException("Delivery", id));

        Delivery updatedDelivery = delivery.startTransit(request.getDriverNotes(), timeProvider.now());
        Delivery savedDelivery = deliveryRepository.save(updatedDelivery);

        return deliveryDtoAssembler.toDto(savedDelivery);
    }
}

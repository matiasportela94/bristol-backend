package com.bristol.application.delivery.usecase;

import com.bristol.application.delivery.dto.DeliveryDto;
import com.bristol.application.delivery.dto.MarkDeliveryFailedRequest;
import com.bristol.domain.delivery.Delivery;
import com.bristol.domain.delivery.DeliveryId;
import com.bristol.domain.delivery.DeliveryRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Use case to mark delivery as failed.
 */
@Service
@RequiredArgsConstructor
public class MarkDeliveryFailedUseCase {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryDtoAssembler deliveryDtoAssembler;

    @Transactional
    public DeliveryDto execute(String id, MarkDeliveryFailedRequest request) {
        DeliveryId deliveryId = new DeliveryId(UUID.fromString(id));
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NotFoundException("Delivery", id));

        Delivery updatedDelivery = delivery.markAsFailed(request.getDriverNotes(), Instant.now());
        Delivery savedDelivery = deliveryRepository.save(updatedDelivery);

        return deliveryDtoAssembler.toDto(savedDelivery);
    }
}

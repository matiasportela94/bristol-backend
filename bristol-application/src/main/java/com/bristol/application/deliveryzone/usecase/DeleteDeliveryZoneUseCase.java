package com.bristol.application.deliveryzone.usecase;

import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.delivery.DeliveryZoneRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use case to delete delivery zone.
 */
@Service
@RequiredArgsConstructor
public class DeleteDeliveryZoneUseCase {

    private final DeliveryZoneRepository deliveryZoneRepository;

    @Transactional
    public void execute(String id) {
        DeliveryZoneId zoneId = new DeliveryZoneId(UUID.fromString(id));

        // Verify zone exists before deleting
        deliveryZoneRepository.findById(zoneId)
                .orElseThrow(() -> new NotFoundException("DeliveryZone", id));

        deliveryZoneRepository.delete(zoneId);
    }
}

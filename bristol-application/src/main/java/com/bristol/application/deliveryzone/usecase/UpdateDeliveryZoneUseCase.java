package com.bristol.application.deliveryzone.usecase;

import com.bristol.application.deliveryzone.dto.DeliveryZoneDto;
import com.bristol.application.deliveryzone.dto.UpdateDeliveryZoneRequest;
import com.bristol.domain.delivery.DeliveryZone;
import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.delivery.DeliveryZoneRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Use case to update delivery zone.
 */
@Service
@RequiredArgsConstructor
public class UpdateDeliveryZoneUseCase {

    private final DeliveryZoneRepository deliveryZoneRepository;
    private final DeliveryZoneMapper deliveryZoneMapper;

    @Transactional
    public DeliveryZoneDto execute(String id, UpdateDeliveryZoneRequest request) {
        DeliveryZoneId zoneId = new DeliveryZoneId(UUID.fromString(id));
        DeliveryZone zone = deliveryZoneRepository.findById(zoneId)
                .orElseThrow(() -> new NotFoundException("DeliveryZone", id));

        DeliveryZone updatedZone = zone.update(request.getName(), request.getDescription(), Instant.now());
        DeliveryZone savedZone = deliveryZoneRepository.save(updatedZone);

        return deliveryZoneMapper.toDto(savedZone);
    }
}

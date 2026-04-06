package com.bristol.application.deliveryzone.usecase;

import com.bristol.application.deliveryzone.dto.DeliveryZoneDto;
import com.bristol.domain.delivery.DeliveryZone;
import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.delivery.DeliveryZoneRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use case to get delivery zone by ID.
 */
@Service
@RequiredArgsConstructor
public class GetDeliveryZoneByIdUseCase {

    private final DeliveryZoneRepository deliveryZoneRepository;
    private final DeliveryZoneMapper deliveryZoneMapper;

    @Transactional(readOnly = true)
    public DeliveryZoneDto execute(String id) {
        DeliveryZoneId zoneId = new DeliveryZoneId(UUID.fromString(id));
        DeliveryZone zone = deliveryZoneRepository.findById(zoneId)
                .orElseThrow(() -> new NotFoundException("DeliveryZone", id));
        return deliveryZoneMapper.toDto(zone);
    }
}

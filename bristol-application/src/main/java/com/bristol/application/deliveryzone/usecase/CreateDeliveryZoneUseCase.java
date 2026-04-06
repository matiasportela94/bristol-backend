package com.bristol.application.deliveryzone.usecase;

import com.bristol.application.deliveryzone.dto.CreateDeliveryZoneRequest;
import com.bristol.application.deliveryzone.dto.DeliveryZoneDto;
import com.bristol.domain.delivery.DeliveryZone;
import com.bristol.domain.delivery.DeliveryZoneRepository;
import com.bristol.domain.shared.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CreateDeliveryZoneUseCase {
    private final DeliveryZoneRepository deliveryZoneRepository;
    private final DeliveryZoneMapper deliveryZoneMapper;

    @Transactional
    public DeliveryZoneDto execute(CreateDeliveryZoneRequest request) {
        DeliveryZone zone = DeliveryZone.create(
                request.getName(),
                request.getDescription(),
                Instant.now()
        );

        DeliveryZone saved = deliveryZoneRepository.save(zone);
        return deliveryZoneMapper.toDto(saved);
    }
}

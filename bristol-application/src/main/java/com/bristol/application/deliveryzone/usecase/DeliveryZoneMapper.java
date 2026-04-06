package com.bristol.application.deliveryzone.usecase;

import com.bristol.application.deliveryzone.dto.DeliveryZoneDto;
import com.bristol.domain.delivery.DeliveryZone;
import org.springframework.stereotype.Component;

@Component
public class DeliveryZoneMapper {
    public DeliveryZoneDto toDto(DeliveryZone zone) {
        return DeliveryZoneDto.builder()
                .id(zone.getId().getValue().toString())
                .name(zone.getName())
                .description(zone.getDescription())
                .baseCost(null)
                .costPerKm(null)
                .maxDistanceKm(null)
                .active(zone.isActive())
                .createdAt(zone.getCreatedAt())
                .updatedAt(zone.getUpdatedAt())
                .build();
    }
}

package com.bristol.application.deliveryzone.usecase;

import com.bristol.application.deliveryzone.dto.DeliveryZoneDto;
import com.bristol.domain.delivery.DeliveryZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAllDeliveryZonesUseCase {
    private final DeliveryZoneRepository deliveryZoneRepository;
    private final DeliveryZoneMapper deliveryZoneMapper;

    @Cacheable("deliveryZones")
    @Transactional(readOnly = true)
    public List<DeliveryZoneDto> execute() {
        return deliveryZoneRepository.findAll().stream()
                .map(deliveryZoneMapper::toDto)
                .collect(Collectors.toList());
    }
}

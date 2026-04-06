package com.bristol.application.delivery.usecase;

import com.bristol.application.delivery.dto.DeliveryDto;
import com.bristol.domain.delivery.DeliveryId;
import com.bristol.domain.delivery.DeliveryRepository;
import com.bristol.domain.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetDeliveryByIdUseCase {
    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;

    @Transactional(readOnly = true)
    public DeliveryDto execute(String deliveryId) {
        DeliveryId id = new DeliveryId(deliveryId);
        return deliveryRepository.findById(id)
                .map(deliveryMapper::toDto)
                .orElseThrow(() -> new ValidationException("Delivery not found: " + deliveryId));
    }
}

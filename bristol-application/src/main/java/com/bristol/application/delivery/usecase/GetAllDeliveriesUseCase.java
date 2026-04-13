package com.bristol.application.delivery.usecase;

import com.bristol.application.delivery.dto.DeliveryDto;
import com.bristol.domain.delivery.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllDeliveriesUseCase {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryDtoAssembler deliveryDtoAssembler;

    @Transactional(readOnly = true)
    public List<DeliveryDto> execute() {
        return deliveryDtoAssembler.toDtos(deliveryRepository.findAll());
    }
}

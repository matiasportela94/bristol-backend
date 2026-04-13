package com.bristol.application.delivery.usecase;

import com.bristol.application.delivery.dto.DeliveryDto;
import com.bristol.domain.delivery.DeliveryRepository;
import com.bristol.domain.order.OrderId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetDeliveriesByOrderUseCase {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryDtoAssembler deliveryDtoAssembler;

    @Transactional(readOnly = true)
    public List<DeliveryDto> execute(String orderId) {
        OrderId targetOrderId = new OrderId(orderId);

        return deliveryRepository.findAll().stream()
                .filter(delivery -> delivery.getOrderId().equals(targetOrderId))
                .map(deliveryDtoAssembler::toDto)
                .collect(Collectors.toList());
    }
}

package com.bristol.application.delivery.usecase;

import com.bristol.application.delivery.dto.DeliveryDto;
import com.bristol.domain.delivery.DeliveryRepository;
import com.bristol.domain.order.OrderRepository;
import com.bristol.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetDeliveriesByUserEmailUseCase {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final DeliveryDtoAssembler deliveryDtoAssembler;

    @Transactional(readOnly = true)
    public List<DeliveryDto> execute(String email) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    Set<String> orderIds = orderRepository.findByUserId(user.getId()).stream()
                            .map(order -> order.getId().getValue().toString())
                            .collect(Collectors.toSet());

                    return deliveryRepository.findAll().stream()
                            .filter(delivery -> orderIds.contains(delivery.getOrderId().getValue().toString()))
                            .map(deliveryDtoAssembler::toDto)
                            .collect(Collectors.toList());
                })
                .orElse(Collections.emptyList());
    }
}

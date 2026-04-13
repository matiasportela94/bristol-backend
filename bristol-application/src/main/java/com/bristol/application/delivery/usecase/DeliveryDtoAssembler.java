package com.bristol.application.delivery.usecase;

import com.bristol.application.delivery.dto.DeliveryDto;
import com.bristol.domain.delivery.Delivery;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderRepository;
import com.bristol.domain.user.User;
import com.bristol.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DeliveryDtoAssembler {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final DeliveryMapper deliveryMapper;

    public DeliveryDto toDto(Delivery delivery) {
        Order order = orderRepository.findById(delivery.getOrderId()).orElse(null);
        User user = order != null ? userRepository.findById(order.getUserId()).orElse(null) : null;
        return deliveryMapper.toDto(delivery, order, user);
    }

    public List<DeliveryDto> toDtos(List<Delivery> deliveries) {
        Map<UUID, Order> ordersById = orderRepository.findAll().stream()
                .collect(Collectors.toMap(order -> order.getId().getValue(), Function.identity()));

        Map<UUID, User> usersById = userRepository.findAll().stream()
                .collect(Collectors.toMap(user -> user.getId().getValue(), Function.identity()));

        return deliveries.stream()
                .map(delivery -> {
                    Order order = ordersById.get(delivery.getOrderId().getValue());
                    User user = order != null ? usersById.get(order.getUserId().getValue()) : null;
                    return deliveryMapper.toDto(delivery, order, user);
                })
                .collect(Collectors.toList());
    }
}

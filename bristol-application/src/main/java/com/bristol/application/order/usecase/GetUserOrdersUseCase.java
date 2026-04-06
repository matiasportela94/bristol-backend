package com.bristol.application.order.usecase;

import com.bristol.application.order.dto.OrderDto;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderRepository;
import com.bristol.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use case to retrieve all orders for a specific user.
 */
@Service
@RequiredArgsConstructor
public class GetUserOrdersUseCase {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Transactional(readOnly = true)
    public List<OrderDto> execute(String userId) {
        UserId id = new UserId(userId);
        List<Order> orders = orderRepository.findByUserId(id);
        return orders.stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }
}

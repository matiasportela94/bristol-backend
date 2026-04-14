package com.bristol.application.order.usecase;

import com.bristol.application.order.dto.OrderDto;
import com.bristol.domain.distributor.DistributorRepository;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderId;
import com.bristol.domain.order.OrderRepository;
import com.bristol.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case to retrieve a single order by ID.
 */
@Service
@RequiredArgsConstructor
public class GetOrderByIdUseCase {

    private final OrderRepository orderRepository;
    private final DistributorRepository distributorRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    @Transactional(readOnly = true)
    public OrderDto execute(String orderId) {
        OrderId id = new OrderId(orderId);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        return orderMapper.toDto(
                order,
                resolveCustomerName(order),
                resolveUserEmail(order),
                resolveDistributorName(order)
        );
    }

    private String resolveDistributorName(Order order) {
        if (order.getDistributorId() == null) {
            return null;
        }

        return distributorRepository.findById(order.getDistributorId())
                .map(distributor -> distributor.getRazonSocial())
                .orElse(null);
    }

    private String resolveCustomerName(Order order) {
        return userRepository.findById(order.getUserId())
                .map(user -> formatCustomerName(user.getFirstName(), user.getLastName(), user.getEmail()))
                .orElse(null);
    }

    private String resolveUserEmail(Order order) {
        return userRepository.findById(order.getUserId())
                .map(user -> user.getEmail())
                .orElse(null);
    }

    private String formatCustomerName(String firstName, String lastName, String email) {
        String safeFirstName = firstName != null ? firstName : "";
        String safeLastName = lastName != null ? lastName : "";
        String fullName = (safeFirstName + " " + safeLastName).trim();
        return !fullName.isEmpty() ? fullName : email;
    }
}

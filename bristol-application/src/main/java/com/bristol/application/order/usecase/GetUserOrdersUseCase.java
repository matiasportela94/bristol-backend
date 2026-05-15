package com.bristol.application.order.usecase;

import com.bristol.application.order.dto.OrderDto;
import com.bristol.domain.delivery.Delivery;
import com.bristol.domain.delivery.DeliveryRepository;
import com.bristol.domain.distributor.DistributorRepository;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderRepository;
import com.bristol.domain.user.UserRepository;
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
    private final DeliveryRepository deliveryRepository;
    private final DistributorRepository distributorRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    @Transactional(readOnly = true)
    public List<OrderDto> execute(String userId) {
        UserId id = new UserId(userId);
        List<Order> orders = orderRepository.findByUserId(id);
        return orders.stream()
                .map(order -> {
                    Delivery delivery = deliveryRepository.findByOrderId(order.getId()).orElse(null);
                    return orderMapper.toDto(
                            order,
                            resolveCustomerName(order),
                            resolveUserEmail(order),
                            resolveDistributorName(order),
                            delivery
                    );
                })
                .collect(Collectors.toList());
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

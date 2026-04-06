package com.bristol.application.order.usecase;

import com.bristol.application.order.dto.AssignDistributorRequest;
import com.bristol.application.order.dto.OrderDto;
import com.bristol.domain.distributor.DistributorId;
import com.bristol.domain.distributor.DistributorRepository;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderId;
import com.bristol.domain.order.OrderRepository;
import com.bristol.domain.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AssignOrderToDistributorUseCase {
    private final OrderRepository orderRepository;
    private final DistributorRepository distributorRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderDto execute(String orderId, AssignDistributorRequest request) {
        OrderId id = new OrderId(orderId);
        DistributorId distributorId = new DistributorId(request.getDistributorId());
        Instant now = Instant.now();

        // Verify distributor exists
        distributorRepository.findById(distributorId)
                .orElseThrow(() -> new ValidationException("Distributor not found: " + request.getDistributorId()));

        // Find and assign order
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Order not found: " + orderId));

        Order assignedOrder = order.assignToDistributor(distributorId, now);

        Order savedOrder = orderRepository.save(assignedOrder);
        return orderMapper.toDto(savedOrder);
    }
}

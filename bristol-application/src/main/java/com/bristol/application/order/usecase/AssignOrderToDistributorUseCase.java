package com.bristol.application.order.usecase;

import com.bristol.application.order.dto.AssignDistributorRequest;
import com.bristol.application.order.dto.OrderDto;
import com.bristol.domain.distributor.DistributorId;
import com.bristol.domain.distributor.DistributorRepository;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderId;
import com.bristol.domain.order.OrderRepository;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
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
    private final TimeProvider timeProvider;

    @Transactional
    public OrderDto execute(String orderId, AssignDistributorRequest request) {
        OrderId id = new OrderId(orderId);
        DistributorId distributorId = new DistributorId(request.getDistributorId());
        Instant now = timeProvider.now();

        // Verify distributor exists
        String distributorName = distributorRepository.findById(distributorId)
                .map(distributor -> distributor.getRazonSocial())
                .orElseThrow(() -> new ValidationException("Distributor not found: " + request.getDistributorId()));

        // Find and assign order
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Order not found: " + orderId));

        Order assignedOrder = order.assignToDistributor(distributorId, now);

        Order savedOrder = orderRepository.save(assignedOrder);
        return orderMapper.toDto(savedOrder, distributorName);
    }
}

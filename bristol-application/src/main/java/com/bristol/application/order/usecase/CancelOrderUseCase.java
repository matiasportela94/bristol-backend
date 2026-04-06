package com.bristol.application.order.usecase;

import com.bristol.application.order.dto.OrderDto;
import com.bristol.application.order.service.StockManagementService;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderId;
import com.bristol.domain.order.OrderRepository;
import com.bristol.domain.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Use case to cancel an order.
 */
@Service
@RequiredArgsConstructor
public class CancelOrderUseCase {

    private final OrderRepository orderRepository;
    private final StockManagementService stockManagementService;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderDto execute(String orderId) {
        OrderId id = new OrderId(orderId);
        Instant now = Instant.now();

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Order not found: " + orderId));

        // Check if order can be cancelled
        if (!order.isCancellable()) {
            throw new ValidationException(
                    "Order cannot be cancelled. Current status: " + order.getStatus());
        }

        // Restore stock if it was deducted
        if (order.isStockUpdated()) {
            stockManagementService.restoreStockForOrder(order);
        }

        // Cancel the order
        Order cancelledOrder = order.cancel(now);
        Order savedOrder = orderRepository.save(cancelledOrder);

        return orderMapper.toDto(savedOrder);
    }
}

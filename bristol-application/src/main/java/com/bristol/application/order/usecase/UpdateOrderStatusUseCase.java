package com.bristol.application.order.usecase;

import com.bristol.application.order.dto.OrderDto;
import com.bristol.application.order.dto.UpdateOrderStatusRequest;
import com.bristol.application.order.service.StockManagementService;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderId;
import com.bristol.domain.order.OrderRepository;
import com.bristol.domain.order.OrderStatus;
import com.bristol.domain.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Use case to update order status.
 */
@Service
@RequiredArgsConstructor
public class UpdateOrderStatusUseCase {

    private final OrderRepository orderRepository;
    private final StockManagementService stockManagementService;
    private final CouponRedemptionApplicationService couponRedemptionApplicationService;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderDto execute(String orderId, UpdateOrderStatusRequest request) {
        OrderId id = new OrderId(orderId);
        Instant now = Instant.now();

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Order not found: " + orderId));

        OrderStatus targetStatus = request.getStatus();
        Order updatedOrder = updateOrderToStatus(order, targetStatus, now);

        Order savedOrder = orderRepository.save(updatedOrder);
        synchronizeCouponRedemptions(savedOrder, targetStatus, now);
        return orderMapper.toDto(savedOrder);
    }

    private Order updateOrderToStatus(Order order, OrderStatus targetStatus, Instant now) {
        // Apply the appropriate domain method based on target status
        return switch (targetStatus) {
            case PAYMENT_IN_PROCESS -> order.markPaymentInProcess(now);
            case PAID -> {
                Order paidOrder = order.markAsPaid(now);
                // Deduct stock when order is paid
                stockManagementService.deductStockForOrder(paidOrder);
                yield paidOrder.markStockAsUpdated(now);
            }
            case PROCESSING -> order.startProcessing(now);
            case SHIPPED -> order.markAsShipped(now);
            case DELIVERED -> order.markAsDelivered(now);
            case PAYMENT_FAILED -> order.markPaymentFailed(now);
            case CANCELLED -> {
                // Restore stock if it was deducted
                if (order.isStockUpdated()) {
                    stockManagementService.restoreStockForOrder(order);
                }
                yield order.cancel(now);
            }
            case PENDING_PAYMENT -> throw new ValidationException(
                    "Cannot manually set order to pending payment status");
        };
    }

    private void synchronizeCouponRedemptions(Order order, OrderStatus targetStatus, Instant now) {
        switch (targetStatus) {
            case PAID -> couponRedemptionApplicationService.recordPaidOrderRedemptions(order, now);
            case CANCELLED, PAYMENT_FAILED -> couponRedemptionApplicationService.clearOrderRedemptions(order, now);
            default -> {
            }
        }
    }
}

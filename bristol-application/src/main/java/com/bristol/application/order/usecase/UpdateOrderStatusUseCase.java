package com.bristol.application.order.usecase;

import com.bristol.application.delivery.service.DeliverySchedulingService;
import com.bristol.application.order.dto.OrderDto;
import com.bristol.application.order.dto.UpdateOrderStatusRequest;
import com.bristol.application.order.service.StockManagementService;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderId;
import com.bristol.domain.order.OrderRepository;
import com.bristol.domain.order.OrderStatus;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
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
    private final DeliverySchedulingService deliverySchedulingService;
    private final OrderMapper orderMapper;
    private final TimeProvider timeProvider;

    @Transactional
    public OrderDto execute(String orderId, UpdateOrderStatusRequest request) {
        OrderId id = new OrderId(orderId);
        Instant now = timeProvider.now();

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Order not found: " + orderId));

        OrderStatus targetStatus = request.getStatus();
        Order updatedOrder = updateOrderToStatus(order, targetStatus, now);

        Order savedOrder = orderRepository.save(updatedOrder);
        synchronizeCouponRedemptions(savedOrder, targetStatus, now);
        synchronizeDelivery(savedOrder, targetStatus);
        return orderMapper.toDto(savedOrder);
    }

    private Order updateOrderToStatus(Order order, OrderStatus targetStatus, Instant now) {
        // Apply the appropriate domain method based on target status
        return switch (targetStatus) {
            case PAYMENT_IN_PROCESS -> order.markPaymentInProcess(now);
            case PAID -> {
                yield order.markAsPaid(now);
            }
            case PROCESSING -> order.startProcessing(now);
            case SHIPPED -> order.markAsShipped(now);
            case DELIVERED -> order.markAsDelivered(now);
            case PAYMENT_FAILED -> {
                if (order.isStockUpdated()) {
                    stockManagementService.restoreStockForOrder(order);
                }
                yield order.markPaymentFailed(now);
            }
            case CANCELLED -> {
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

    private void synchronizeDelivery(Order order, OrderStatus targetStatus) {
        switch (targetStatus) {
            case PAID -> deliverySchedulingService.ensureScheduledForPaidOrder(order);
            case CANCELLED, PAYMENT_FAILED -> deliverySchedulingService.cancelScheduledDelivery(order);
            default -> {
            }
        }
    }
}

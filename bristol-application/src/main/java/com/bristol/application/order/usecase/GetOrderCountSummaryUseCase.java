package com.bristol.application.order.usecase;

import com.bristol.application.order.dto.OrderCountSummaryDto;
import com.bristol.domain.order.OrderRepository;
import com.bristol.domain.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case to get order count summary by status.
 */
@Service
@RequiredArgsConstructor
public class GetOrderCountSummaryUseCase {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public OrderCountSummaryDto execute() {
        return OrderCountSummaryDto.builder()
                .totalOrders(orderRepository.countByStatus(null))
                .pendingPaymentOrders(orderRepository.countByStatus(OrderStatus.PENDING_PAYMENT))
                .paymentInProcessOrders(orderRepository.countByStatus(OrderStatus.PAYMENT_IN_PROCESS))
                .paidOrders(orderRepository.countByStatus(OrderStatus.PAID))
                .processingOrders(orderRepository.countByStatus(OrderStatus.PROCESSING))
                .shippedOrders(orderRepository.countByStatus(OrderStatus.SHIPPED))
                .deliveredOrders(orderRepository.countByStatus(OrderStatus.DELIVERED))
                .cancelledOrders(orderRepository.countByStatus(OrderStatus.CANCELLED))
                .paymentFailedOrders(orderRepository.countByStatus(OrderStatus.PAYMENT_FAILED))
                .build();
    }
}

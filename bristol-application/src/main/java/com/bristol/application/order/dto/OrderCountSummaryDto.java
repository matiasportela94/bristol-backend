package com.bristol.application.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for order count summary by status.
 * Matches the actual OrderStatus enum values in the domain.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCountSummaryDto {
    private long totalOrders;
    private long pendingPaymentOrders;
    private long paymentInProcessOrders;
    private long paidOrders;
    private long processingOrders;
    private long shippedOrders;
    private long deliveredOrders;
    private long cancelledOrders;
    private long paymentFailedOrders;
}

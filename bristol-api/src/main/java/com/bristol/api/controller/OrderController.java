package com.bristol.api.controller;

import com.bristol.application.order.dto.*;
import com.bristol.application.order.usecase.*;
import com.bristol.application.shared.dto.PagedResponse;
import com.bristol.domain.order.OrderStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for order endpoints.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderByIdUseCase getOrderByIdUseCase;
    private final GetUserOrdersUseCase getUserOrdersUseCase;
    private final GetFilteredOrdersUseCase getFilteredOrdersUseCase;
    private final GetFilteredOrdersPaginatedUseCase getFilteredOrdersPaginatedUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final ApplyOrderCouponUseCase applyOrderCouponUseCase;
    private final ApplyShippingCouponUseCase applyShippingCouponUseCase;
    private final RepriceOrderPromotionsUseCase repriceOrderPromotionsUseCase;
    private final AssignOrderToDistributorUseCase assignOrderToDistributorUseCase;
    private final GetOrderCountSummaryUseCase getOrderCountSummaryUseCase;

    /**
     * Create a new order.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Create order", description = "Create a new order")
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderDto order = createOrderUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    /**
     * Get order by ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get order by ID", description = "Retrieve a single order by its ID")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable String id) {
        OrderDto order = getOrderByIdUseCase.execute(id);
        return ResponseEntity.ok(order);
    }

    /**
     * Get all orders for a specific user.
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get user orders", description = "Retrieve all orders for a specific user")
    public ResponseEntity<List<OrderDto>> getUserOrders(@PathVariable String userId) {
        List<OrderDto> orders = getUserOrdersUseCase.execute(userId);
        return ResponseEntity.ok(orders);
    }

    /**
     * Get filtered orders.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get filtered orders", description = "Retrieve orders with optional filters (Admin only)")
    public ResponseEntity<List<OrderDto>> getFilteredOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String distributorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String userId
    ) {
        OrderFilterRequest filter = OrderFilterRequest.builder()
                .status(status)
                .distributorId(distributorId)
                .startDate(startDate)
                .endDate(endDate)
                .userId(userId)
                .build();
        List<OrderDto> orders = getFilteredOrdersUseCase.execute(filter);
        return ResponseEntity.ok(orders);
    }

    /**
     * Get filtered orders with pagination.
     */
    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get filtered orders (paginated)", description = "Retrieve orders with optional filters and pagination (Admin only)")
    public ResponseEntity<PagedResponse<OrderDto>> getFilteredOrdersPaginated(
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String distributorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        OrderFilterRequest filter = OrderFilterRequest.builder()
                .orderId(orderId)
                .status(status)
                .distributorId(distributorId)
                .startDate(startDate)
                .endDate(endDate)
                .userId(userId)
                .build();
        PagedResponse<OrderDto> orders = getFilteredOrdersPaginatedUseCase.execute(filter, page, size);
        return ResponseEntity.ok(orders);
    }

    /**
     * Get order count summary by status.
     */
    @GetMapping("/count-summary")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get order count summary", description = "Get order counts grouped by status (Admin only)")
    public ResponseEntity<OrderCountSummaryDto> getOrderCountSummary() {
        OrderCountSummaryDto summary = getOrderCountSummaryUseCase.execute();
        return ResponseEntity.ok(summary);
    }

    /**
     * Update order status.
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update order status", description = "Update the status of an order (Admin only)")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        OrderDto order = updateOrderStatusUseCase.execute(id, request);
        return ResponseEntity.ok(order);
    }

    /**
     * Cancel an order.
     */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Cancel order", description = "Cancel an order and restore stock if applicable")
    public ResponseEntity<OrderDto> cancelOrder(@PathVariable String id) {
        OrderDto order = cancelOrderUseCase.execute(id);
        return ResponseEntity.ok(order);
    }

    /**
     * Apply order coupon.
     */
    @PostMapping("/{id}/apply-coupon")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Apply order coupon", description = "Apply a discount coupon to the order")
    public ResponseEntity<OrderDto> applyOrderCoupon(
            @PathVariable String id,
            @Valid @RequestBody ApplyCouponRequest request
    ) {
        OrderDto order = applyOrderCouponUseCase.execute(id, request);
        return ResponseEntity.ok(order);
    }

    /**
     * Apply shipping coupon.
     */
    @PostMapping("/{id}/apply-shipping-coupon")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Apply shipping coupon", description = "Apply a discount coupon to shipping cost")
    public ResponseEntity<OrderDto> applyShippingCoupon(
            @PathVariable String id,
            @Valid @RequestBody ApplyCouponRequest request
    ) {
        OrderDto order = applyShippingCouponUseCase.execute(id, request);
        return ResponseEntity.ok(order);
    }

    /**
     * Reprice promotions for an order.
     */
    @PostMapping("/{id}/reprice-promotions")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Reprice order promotions", description = "Re-evaluate manual and automatic promotions for an order")
    public ResponseEntity<OrderDto> repriceOrderPromotions(@PathVariable String id) {
        OrderDto order = repriceOrderPromotionsUseCase.execute(id);
        return ResponseEntity.ok(order);
    }

    /**
     * Assign order to distributor.
     */
    @PutMapping("/{id}/assign-distributor")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign order to distributor", description = "Assign a paid order to a distributor (Admin only)")
    public ResponseEntity<OrderDto> assignOrderToDistributor(
            @PathVariable String id,
            @Valid @RequestBody AssignDistributorRequest request
    ) {
        OrderDto order = assignOrderToDistributorUseCase.execute(id, request);
        return ResponseEntity.ok(order);
    }
}

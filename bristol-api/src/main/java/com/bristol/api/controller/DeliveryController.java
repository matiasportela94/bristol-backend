package com.bristol.api.controller;

import com.bristol.application.delivery.dto.*;
import com.bristol.application.delivery.usecase.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
@Tag(name = "Deliveries", description = "Delivery management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class DeliveryController {

    private final GetFilteredDeliveriesUseCase getFilteredDeliveriesUseCase;
    private final GetDeliveriesByOrderUseCase getDeliveriesByOrderUseCase;
    private final GetDeliveriesByUserEmailUseCase getDeliveriesByUserEmailUseCase;
    private final GetDeliveryByIdUseCase getDeliveryByIdUseCase;
    private final RescheduleDeliveryUseCase rescheduleDeliveryUseCase;
    private final StartDeliveryTransitUseCase startDeliveryTransitUseCase;
    private final CompleteDeliveryUseCase completeDeliveryUseCase;
    private final MarkDeliveryFailedUseCase markDeliveryFailedUseCase;

    /**
     * Get deliveries with optional date filters.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get deliveries", description = "Retrieve all deliveries or filter by scheduledDate/dateFrom/dateTo (Admin only)")
    public ResponseEntity<DeliveryPageDto> getDeliveries(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate scheduledDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) String orderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(getFilteredDeliveriesUseCase.execute(scheduledDate, dateFrom, dateTo, orderId, page, size));
    }

    /**
     * Get deliveries by order ID.
     */
    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get deliveries by order", description = "Retrieve deliveries linked to an order")
    public ResponseEntity<java.util.List<DeliveryDto>> getDeliveriesByOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(getDeliveriesByOrderUseCase.execute(orderId));
    }

    /**
     * Get deliveries by user email.
     */
    @GetMapping("/user/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get deliveries by user email", description = "Retrieve deliveries for a user email")
    public ResponseEntity<java.util.List<DeliveryDto>> getDeliveriesByUserEmail(@PathVariable String email) {
        return ResponseEntity.ok(getDeliveriesByUserEmailUseCase.execute(email));
    }

    /**
     * Get delivery by ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get delivery by ID", description = "Retrieve a delivery by its ID")
    public ResponseEntity<DeliveryDto> getDeliveryById(@PathVariable String id) {
        return ResponseEntity.ok(getDeliveryByIdUseCase.execute(id));
    }

    /**
     * Reschedule delivery.
     */
    @PutMapping("/{id}/reschedule")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reschedule delivery", description = "Reschedule a delivery to a specific date (Admin only)")
    public ResponseEntity<DeliveryDto> rescheduleDelivery(
            @PathVariable String id,
            @Valid @RequestBody RescheduleDeliveryRequest request
    ) {
        return ResponseEntity.ok(rescheduleDeliveryUseCase.execute(id, request));
    }

    /**
     * Start delivery transit.
     */
    @PutMapping("/{id}/start-transit")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Start delivery transit", description = "Mark delivery as in transit (Admin only)")
    public ResponseEntity<DeliveryDto> startTransit(
            @PathVariable String id,
            @Valid @RequestBody StartTransitRequest request
    ) {
        DeliveryDto delivery = startDeliveryTransitUseCase.execute(id, request);
        return ResponseEntity.ok(delivery);
    }

    /**
     * Complete delivery.
     */
    @PutMapping("/{id}/complete")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Complete delivery", description = "Mark delivery as completed (Admin only)")
    public ResponseEntity<DeliveryDto> completeDelivery(
            @PathVariable String id,
            @Valid @RequestBody CompleteDeliveryRequest request
    ) {
        DeliveryDto delivery = completeDeliveryUseCase.execute(id, request);
        return ResponseEntity.ok(delivery);
    }

    /**
     * Mark delivery as failed.
     */
    @PutMapping("/{id}/mark-failed")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mark delivery as failed", description = "Mark delivery as failed (Admin only)")
    public ResponseEntity<DeliveryDto> markAsFailed(
            @PathVariable String id,
            @Valid @RequestBody MarkDeliveryFailedRequest request
    ) {
        DeliveryDto delivery = markDeliveryFailedUseCase.execute(id, request);
        return ResponseEntity.ok(delivery);
    }
}

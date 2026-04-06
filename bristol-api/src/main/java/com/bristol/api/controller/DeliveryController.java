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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
@Tag(name = "Deliveries", description = "Delivery management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class DeliveryController {

    private final GetDeliveryByIdUseCase getDeliveryByIdUseCase;
    private final StartDeliveryTransitUseCase startDeliveryTransitUseCase;
    private final CompleteDeliveryUseCase completeDeliveryUseCase;
    private final MarkDeliveryFailedUseCase markDeliveryFailedUseCase;

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

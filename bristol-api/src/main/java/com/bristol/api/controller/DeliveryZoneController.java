package com.bristol.api.controller;

import com.bristol.application.deliveryzone.dto.CreateDeliveryZoneRequest;
import com.bristol.application.deliveryzone.dto.DeliveryZoneDto;
import com.bristol.application.deliveryzone.dto.UpdateDeliveryZoneRequest;
import com.bristol.application.deliveryzone.usecase.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for delivery zone endpoints.
 */
@RestController
@RequestMapping("/api/delivery-zones")
@RequiredArgsConstructor
@Tag(name = "Delivery Zones", description = "Delivery zone management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class DeliveryZoneController {

    private final CreateDeliveryZoneUseCase createDeliveryZoneUseCase;
    private final GetAllDeliveryZonesUseCase getAllDeliveryZonesUseCase;
    private final GetDeliveryZoneByIdUseCase getDeliveryZoneByIdUseCase;
    private final UpdateDeliveryZoneUseCase updateDeliveryZoneUseCase;
    private final DeleteDeliveryZoneUseCase deleteDeliveryZoneUseCase;

    /**
     * Get all delivery zones.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get all delivery zones", description = "Retrieve all delivery zones")
    public ResponseEntity<List<DeliveryZoneDto>> getAllDeliveryZones() {
        return ResponseEntity.ok(getAllDeliveryZonesUseCase.execute());
    }

    /**
     * Get delivery zone by ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get delivery zone by ID", description = "Retrieve a delivery zone by its ID")
    public ResponseEntity<DeliveryZoneDto> getDeliveryZoneById(@PathVariable String id) {
        return ResponseEntity.ok(getDeliveryZoneByIdUseCase.execute(id));
    }

    /**
     * Create a new delivery zone.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create delivery zone", description = "Create a new delivery zone (Admin only)")
    public ResponseEntity<DeliveryZoneDto> createDeliveryZone(@Valid @RequestBody CreateDeliveryZoneRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createDeliveryZoneUseCase.execute(request));
    }

    /**
     * Update delivery zone.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update delivery zone", description = "Update delivery zone information (Admin only)")
    public ResponseEntity<DeliveryZoneDto> updateDeliveryZone(
            @PathVariable String id,
            @Valid @RequestBody UpdateDeliveryZoneRequest request
    ) {
        DeliveryZoneDto zone = updateDeliveryZoneUseCase.execute(id, request);
        return ResponseEntity.ok(zone);
    }

    /**
     * Delete delivery zone.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete delivery zone", description = "Delete a delivery zone (Admin only)")
    public ResponseEntity<Void> deleteDeliveryZone(@PathVariable String id) {
        deleteDeliveryZoneUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}

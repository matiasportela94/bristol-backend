package com.bristol.api.controller;

import com.bristol.application.catalog.merchtype.dto.CreateMerchTypeRequest;
import com.bristol.application.catalog.merchtype.dto.MerchTypeDto;
import com.bristol.application.catalog.merchtype.dto.UpdateMerchTypeRequest;
import com.bristol.application.catalog.merchtype.usecase.*;
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
import java.util.UUID;

/**
 * REST controller for merch type catalog endpoints.
 */
@RestController
@RequestMapping("/api/merch-types")
@RequiredArgsConstructor
@Tag(name = "Merch Types", description = "Merch type catalog management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class MerchTypeController {

    private final CreateMerchTypeUseCase createMerchTypeUseCase;
    private final GetActiveMerchTypesUseCase getActiveMerchTypesUseCase;
    private final GetAllMerchTypesUseCase getAllMerchTypesUseCase;
    private final GetMerchTypeByIdUseCase getMerchTypeByIdUseCase;
    private final UpdateMerchTypeUseCase updateMerchTypeUseCase;
    private final DeactivateMerchTypeUseCase deactivateMerchTypeUseCase;

    /**
     * Get all active merch types (for creating products).
     */
    @GetMapping("/active")
    @Operation(summary = "Get active merch types", description = "Retrieve all active merch types")
    public ResponseEntity<List<MerchTypeDto>> getActiveMerchTypes() {
        return ResponseEntity.ok(getActiveMerchTypesUseCase.execute());
    }

    /**
     * Get all merch types (including inactive, admin only).
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all merch types", description = "Retrieve all merch types including inactive ones (Admin only)")
    public ResponseEntity<List<MerchTypeDto>> getAllMerchTypes() {
        return ResponseEntity.ok(getAllMerchTypesUseCase.execute());
    }

    /**
     * Get merch type by ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get merch type by ID", description = "Retrieve a merch type by its ID (Admin only)")
    public ResponseEntity<MerchTypeDto> getMerchTypeById(@PathVariable UUID id) {
        return ResponseEntity.ok(getMerchTypeByIdUseCase.execute(id));
    }

    /**
     * Create a new merch type.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create merch type", description = "Create a new merch type (Admin only)")
    public ResponseEntity<MerchTypeDto> createMerchType(@Valid @RequestBody CreateMerchTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createMerchTypeUseCase.execute(request));
    }

    /**
     * Update merch type.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update merch type", description = "Update merch type information (Admin only)")
    public ResponseEntity<MerchTypeDto> updateMerchType(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateMerchTypeRequest request
    ) {
        MerchTypeDto merchType = updateMerchTypeUseCase.execute(id, request);
        return ResponseEntity.ok(merchType);
    }

    /**
     * Deactivate merch type (soft delete).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate merch type", description = "Deactivate a merch type (Admin only)")
    public ResponseEntity<Void> deactivateMerchType(@PathVariable UUID id) {
        deactivateMerchTypeUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}

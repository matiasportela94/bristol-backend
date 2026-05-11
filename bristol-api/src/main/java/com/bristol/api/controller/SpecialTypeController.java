package com.bristol.api.controller;

import com.bristol.application.catalog.specialtype.dto.CreateSpecialTypeRequest;
import com.bristol.application.catalog.specialtype.dto.SpecialTypeDto;
import com.bristol.application.catalog.specialtype.dto.UpdateSpecialTypeRequest;
import com.bristol.application.catalog.specialtype.usecase.*;
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
 * REST controller for special type catalog endpoints.
 */
@RestController
@RequestMapping("/api/special-types")
@RequiredArgsConstructor
@Tag(name = "Special Types", description = "Special type catalog management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class SpecialTypeController {

    private final CreateSpecialTypeUseCase createSpecialTypeUseCase;
    private final GetActiveSpecialTypesUseCase getActiveSpecialTypesUseCase;
    private final GetAllSpecialTypesUseCase getAllSpecialTypesUseCase;
    private final GetSpecialTypeByIdUseCase getSpecialTypeByIdUseCase;
    private final UpdateSpecialTypeUseCase updateSpecialTypeUseCase;
    private final DeactivateSpecialTypeUseCase deactivateSpecialTypeUseCase;

    /**
     * Get all active special types (for creating products).
     */
    @GetMapping("/active")
    @Operation(summary = "Get active special types", description = "Retrieve all active special types")
    public ResponseEntity<List<SpecialTypeDto>> getActiveSpecialTypes() {
        return ResponseEntity.ok(getActiveSpecialTypesUseCase.execute());
    }

    /**
     * Get all special types (including inactive, admin only).
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all special types", description = "Retrieve all special types including inactive ones (Admin only)")
    public ResponseEntity<List<SpecialTypeDto>> getAllSpecialTypes() {
        return ResponseEntity.ok(getAllSpecialTypesUseCase.execute());
    }

    /**
     * Get special type by ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get special type by ID", description = "Retrieve a special type by its ID (Admin only)")
    public ResponseEntity<SpecialTypeDto> getSpecialTypeById(@PathVariable UUID id) {
        return ResponseEntity.ok(getSpecialTypeByIdUseCase.execute(id));
    }

    /**
     * Create a new special type.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create special type", description = "Create a new special type (Admin only)")
    public ResponseEntity<SpecialTypeDto> createSpecialType(@Valid @RequestBody CreateSpecialTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createSpecialTypeUseCase.execute(request));
    }

    /**
     * Update special type.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update special type", description = "Update special type information (Admin only)")
    public ResponseEntity<SpecialTypeDto> updateSpecialType(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSpecialTypeRequest request
    ) {
        SpecialTypeDto specialType = updateSpecialTypeUseCase.execute(id, request);
        return ResponseEntity.ok(specialType);
    }

    /**
     * Deactivate special type (soft delete).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate special type", description = "Deactivate a special type (Admin only)")
    public ResponseEntity<Void> deactivateSpecialType(@PathVariable UUID id) {
        deactivateSpecialTypeUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}

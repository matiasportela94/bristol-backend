package com.bristol.api.controller;

import com.bristol.application.catalog.beerstyle.dto.BeerStyleDto;
import com.bristol.application.catalog.beerstyle.dto.CreateBeerStyleRequest;
import com.bristol.application.catalog.beerstyle.dto.UpdateBeerStyleRequest;
import com.bristol.application.catalog.beerstyle.usecase.*;
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
 * REST controller for beer style catalog endpoints.
 */
@RestController
@RequestMapping("/api/beer-styles")
@RequiredArgsConstructor
@Tag(name = "Beer Styles", description = "Beer style catalog management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class BeerStyleController {

    private final CreateBeerStyleUseCase createBeerStyleUseCase;
    private final GetActiveBeerStylesUseCase getActiveBeerStylesUseCase;
    private final GetAllBeerStylesUseCase getAllBeerStylesUseCase;
    private final GetBeerStyleByIdUseCase getBeerStyleByIdUseCase;
    private final UpdateBeerStyleUseCase updateBeerStyleUseCase;
    private final DeactivateBeerStyleUseCase deactivateBeerStyleUseCase;

    /**
     * Get all active beer styles (for creating products).
     */
    @GetMapping("/active")
    @Operation(summary = "Get active beer styles", description = "Retrieve all active beer styles")
    public ResponseEntity<List<BeerStyleDto>> getActiveBeerStyles() {
        return ResponseEntity.ok(getActiveBeerStylesUseCase.execute());
    }

    /**
     * Get all beer styles (including inactive, admin only).
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all beer styles", description = "Retrieve all beer styles including inactive ones (Admin only)")
    public ResponseEntity<List<BeerStyleDto>> getAllBeerStyles() {
        return ResponseEntity.ok(getAllBeerStylesUseCase.execute());
    }

    /**
     * Get beer style by ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get beer style by ID", description = "Retrieve a beer style by its ID (Admin only)")
    public ResponseEntity<BeerStyleDto> getBeerStyleById(@PathVariable UUID id) {
        return ResponseEntity.ok(getBeerStyleByIdUseCase.execute(id));
    }

    /**
     * Create a new beer style.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create beer style", description = "Create a new beer style (Admin only)")
    public ResponseEntity<BeerStyleDto> createBeerStyle(@Valid @RequestBody CreateBeerStyleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createBeerStyleUseCase.execute(request));
    }

    /**
     * Update beer style.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update beer style", description = "Update beer style information (Admin only)")
    public ResponseEntity<BeerStyleDto> updateBeerStyle(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateBeerStyleRequest request
    ) {
        BeerStyleDto beerStyle = updateBeerStyleUseCase.execute(id, request);
        return ResponseEntity.ok(beerStyle);
    }

    /**
     * Deactivate beer style (soft delete).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate beer style", description = "Deactivate a beer style (Admin only)")
    public ResponseEntity<Void> deactivateBeerStyle(@PathVariable UUID id) {
        deactivateBeerStyleUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}

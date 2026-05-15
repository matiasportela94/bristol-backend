package com.bristol.api.controller;

import com.bristol.application.brewery.dto.*;
import com.bristol.application.brewery.usecase.*;
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

@RestController
@RequestMapping("/api/brewery")
@RequiredArgsConstructor
@Tag(name = "Brewery Inventory", description = "Can inventory management per beer style")
@SecurityRequirement(name = "Bearer Authentication")
public class BreweryInventoryController {

    private final AddBrewingBatchUseCase addBrewingBatchUseCase;
    private final GetBreweryInventoryUseCase getBreweryInventoryUseCase;
    private final GetBreweryInventoryByStyleUseCase getBreweryInventoryByStyleUseCase;
    private final GetBreweryBatchesUseCase getBreweryBatchesUseCase;
    private final AdjustInventoryUseCase adjustInventoryUseCase;
    private final GetBreweryInventoryMovementsUseCase getBreweryInventoryMovementsUseCase;

    @GetMapping("/inventory")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get full can inventory for all beer styles")
    public ResponseEntity<List<BreweryInventoryDto>> getInventory() {
        return ResponseEntity.ok(getBreweryInventoryUseCase.execute());
    }

    @GetMapping("/inventory/style/{beerStyleId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get can inventory for a specific beer style")
    public ResponseEntity<BreweryInventoryDto> getInventoryByStyle(@PathVariable UUID beerStyleId) {
        return ResponseEntity.ok(getBreweryInventoryByStyleUseCase.execute(beerStyleId));
    }

    @PatchMapping("/inventory/style/{beerStyleId}/adjust")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Manually adjust can inventory total for a beer style (correction)")
    public ResponseEntity<BreweryInventoryDto> adjustInventory(
            @PathVariable UUID beerStyleId,
            @Valid @RequestBody AdjustInventoryRequest request) {
        return ResponseEntity.ok(adjustInventoryUseCase.execute(beerStyleId, request));
    }

    @GetMapping("/batches")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all brewing batches (history)")
    public ResponseEntity<List<BreweryBatchDto>> getBatches() {
        return ResponseEntity.ok(getBreweryBatchesUseCase.execute());
    }

    @GetMapping("/batches/style/{beerStyleId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get brewing batches for a specific beer style")
    public ResponseEntity<List<BreweryBatchDto>> getBatchesByStyle(@PathVariable UUID beerStyleId) {
        return ResponseEntity.ok(getBreweryBatchesUseCase.executeByStyle(beerStyleId));
    }

    @PostMapping("/batches")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Register a new brewing batch — adds cans to inventory for the given style")
    public ResponseEntity<BreweryBatchDto> addBatch(@Valid @RequestBody AddBrewingBatchRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addBrewingBatchUseCase.execute(request));
    }

    @GetMapping("/movements")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all inventory movements across all styles")
    public ResponseEntity<List<BreweryInventoryMovementDto>> getAllMovements() {
        return ResponseEntity.ok(getBreweryInventoryMovementsUseCase.executeAll());
    }

    @GetMapping("/movements/style/{beerStyleId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get inventory movements for a specific beer style")
    public ResponseEntity<List<BreweryInventoryMovementDto>> getMovementsByStyle(@PathVariable UUID beerStyleId) {
        return ResponseEntity.ok(getBreweryInventoryMovementsUseCase.executeByStyle(beerStyleId));
    }

}

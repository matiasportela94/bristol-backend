package com.bristol.api.controller;

import com.bristol.application.product.merch.dto.CreateMerchProductRequest;
import com.bristol.application.product.merch.dto.MerchProductDto;
import com.bristol.application.product.merch.dto.UpdateMerchProductRequest;
import com.bristol.application.product.merch.usecase.CreateMerchProductUseCase;
import com.bristol.application.product.merch.usecase.GetAllMerchProductsUseCase;
import com.bristol.application.product.merch.usecase.GetMerchProductByIdUseCase;
import com.bristol.application.product.merch.usecase.UpdateMerchProductUseCase;
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
 * REST controller for merch product endpoints.
 */
@RestController
@RequestMapping("/api/products/merch")
@RequiredArgsConstructor
@Tag(name = "Merch Products", description = "Merch product management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class MerchProductController {

    private final CreateMerchProductUseCase createMerchProductUseCase;
    private final GetAllMerchProductsUseCase getAllMerchProductsUseCase;
    private final GetMerchProductByIdUseCase getMerchProductByIdUseCase;
    private final UpdateMerchProductUseCase updateMerchProductUseCase;

    /**
     * Get all merch products.
     */
    @GetMapping
    @Operation(summary = "Get all merch products", description = "Retrieve all merch products")
    public ResponseEntity<List<MerchProductDto>> getAllMerchProducts() {
        return ResponseEntity.ok(getAllMerchProductsUseCase.execute());
    }

    /**
     * Get merch product by ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get merch product by ID", description = "Retrieve a specific merch product")
    public ResponseEntity<MerchProductDto> getMerchProductById(@PathVariable String id) {
        return ResponseEntity.ok(getMerchProductByIdUseCase.execute(id));
    }

    /**
     * Create a new merch product.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create merch product", description = "Create a new merch product (admin only)")
    public ResponseEntity<MerchProductDto> createMerchProduct(@Valid @RequestBody CreateMerchProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createMerchProductUseCase.execute(request));
    }

    /**
     * Update an existing merch product.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update merch product", description = "Update an existing merch product (admin only)")
    public ResponseEntity<MerchProductDto> updateMerchProduct(
            @PathVariable String id,
            @Valid @RequestBody UpdateMerchProductRequest request) {
        return ResponseEntity.ok(updateMerchProductUseCase.execute(id, request));
    }
}

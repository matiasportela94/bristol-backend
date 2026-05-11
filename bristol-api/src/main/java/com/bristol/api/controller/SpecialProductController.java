package com.bristol.api.controller;

import com.bristol.application.product.special.dto.CreateSpecialProductRequest;
import com.bristol.application.product.special.dto.SpecialProductDto;
import com.bristol.application.product.special.dto.UpdateSpecialProductRequest;
import com.bristol.application.product.special.usecase.CreateSpecialProductUseCase;
import com.bristol.application.product.special.usecase.GetAllSpecialProductsUseCase;
import com.bristol.application.product.special.usecase.GetSpecialProductByIdUseCase;
import com.bristol.application.product.special.usecase.UpdateSpecialProductUseCase;
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
 * REST controller for special product endpoints.
 */
@RestController
@RequestMapping("/api/products/special")
@RequiredArgsConstructor
@Tag(name = "Special Products", description = "Special product management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class SpecialProductController {

    private final CreateSpecialProductUseCase createSpecialProductUseCase;
    private final GetAllSpecialProductsUseCase getAllSpecialProductsUseCase;
    private final GetSpecialProductByIdUseCase getSpecialProductByIdUseCase;
    private final UpdateSpecialProductUseCase updateSpecialProductUseCase;

    /**
     * Get all special products.
     */
    @GetMapping
    @Operation(summary = "Get all special products", description = "Retrieve all special products")
    public ResponseEntity<List<SpecialProductDto>> getAllSpecialProducts() {
        return ResponseEntity.ok(getAllSpecialProductsUseCase.execute());
    }

    /**
     * Get special product by ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get special product by ID", description = "Retrieve a specific special product")
    public ResponseEntity<SpecialProductDto> getSpecialProductById(@PathVariable String id) {
        return ResponseEntity.ok(getSpecialProductByIdUseCase.execute(id));
    }

    /**
     * Create a new special product.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create special product", description = "Create a new special product (admin only)")
    public ResponseEntity<SpecialProductDto> createSpecialProduct(@Valid @RequestBody CreateSpecialProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createSpecialProductUseCase.execute(request));
    }

    /**
     * Update an existing special product.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update special product", description = "Update an existing special product (admin only)")
    public ResponseEntity<SpecialProductDto> updateSpecialProduct(
            @PathVariable String id,
            @Valid @RequestBody UpdateSpecialProductRequest request) {
        return ResponseEntity.ok(updateSpecialProductUseCase.execute(id, request));
    }
}

package com.bristol.api.controller;

import com.bristol.application.product.beer.dto.BeerProductDto;
import com.bristol.application.product.beer.dto.CreateBeerProductRequest;
import com.bristol.application.product.beer.dto.UpdateBeerProductRequest;
import com.bristol.application.product.beer.usecase.CreateBeerProductUseCase;
import com.bristol.application.product.beer.usecase.GetAllBeerProductsUseCase;
import com.bristol.application.product.beer.usecase.GetBeerProductByIdUseCase;
import com.bristol.application.product.beer.usecase.UpdateBeerProductUseCase;
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
 * REST controller for beer product endpoints.
 */
@RestController
@RequestMapping("/api/products/beer")
@RequiredArgsConstructor
@Tag(name = "Beer Products", description = "Beer product management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class BeerProductController {

    private final CreateBeerProductUseCase createBeerProductUseCase;
    private final GetAllBeerProductsUseCase getAllBeerProductsUseCase;
    private final GetBeerProductByIdUseCase getBeerProductByIdUseCase;
    private final UpdateBeerProductUseCase updateBeerProductUseCase;

    /**
     * Get all beer products.
     */
    @GetMapping
    @Operation(summary = "Get all beer products", description = "Retrieve all beer products")
    public ResponseEntity<List<BeerProductDto>> getAllBeerProducts() {
        return ResponseEntity.ok(getAllBeerProductsUseCase.execute());
    }

    /**
     * Get beer product by ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get beer product by ID", description = "Retrieve a specific beer product")
    public ResponseEntity<BeerProductDto> getBeerProductById(@PathVariable String id) {
        return ResponseEntity.ok(getBeerProductByIdUseCase.execute(id));
    }

    /**
     * Create a new beer product.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create beer product", description = "Create a new beer product (admin only)")
    public ResponseEntity<BeerProductDto> createBeerProduct(@Valid @RequestBody CreateBeerProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createBeerProductUseCase.execute(request));
    }

    /**
     * Update an existing beer product.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update beer product", description = "Update an existing beer product (admin only)")
    public ResponseEntity<BeerProductDto> updateBeerProduct(
            @PathVariable String id,
            @Valid @RequestBody UpdateBeerProductRequest request) {
        return ResponseEntity.ok(updateBeerProductUseCase.execute(id, request));
    }
}

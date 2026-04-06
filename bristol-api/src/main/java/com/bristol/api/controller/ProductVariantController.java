package com.bristol.api.controller;

import com.bristol.application.productvariant.dto.CreateProductVariantRequest;
import com.bristol.application.productvariant.dto.ProductVariantDto;
import com.bristol.application.productvariant.dto.UpdateProductVariantRequest;
import com.bristol.application.productvariant.dto.UpdateProductVariantStockRequest;
import com.bristol.application.productvariant.usecase.*;
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
 * REST controller for product variant endpoints.
 */
@RestController
@RequestMapping("/api/product-variants")
@RequiredArgsConstructor
@Tag(name = "Product Variants", description = "Product variant management endpoints")
public class ProductVariantController {

    private final GetProductVariantByIdUseCase getProductVariantByIdUseCase;
    private final GetProductVariantsByProductIdUseCase getProductVariantsByProductIdUseCase;
    private final CreateProductVariantUseCase createProductVariantUseCase;
    private final UpdateProductVariantUseCase updateProductVariantUseCase;
    private final UpdateProductVariantStockUseCase updateProductVariantStockUseCase;
    private final DeleteProductVariantUseCase deleteProductVariantUseCase;

    /**
     * Get product variant by ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get product variant by ID", description = "Retrieve a single product variant by its ID")
    public ResponseEntity<ProductVariantDto> getProductVariantById(@PathVariable String id) {
        ProductVariantDto variant = getProductVariantByIdUseCase.execute(id);
        return ResponseEntity.ok(variant);
    }

    /**
     * Get all variants for a specific product.
     */
    @GetMapping("/product/{productId}")
    @Operation(summary = "Get variants by product ID", description = "Retrieve all variants for a specific product")
    public ResponseEntity<List<ProductVariantDto>> getProductVariantsByProductId(@PathVariable String productId) {
        List<ProductVariantDto> variants = getProductVariantsByProductIdUseCase.execute(productId);
        return ResponseEntity.ok(variants);
    }

    /**
     * Create a new product variant.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Create product variant", description = "Create a new product variant (Admin only)")
    public ResponseEntity<ProductVariantDto> createProductVariant(@Valid @RequestBody CreateProductVariantRequest request) {
        ProductVariantDto variant = createProductVariantUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(variant);
    }

    /**
     * Update an existing product variant.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update product variant", description = "Update an existing product variant (Admin only)")
    public ResponseEntity<ProductVariantDto> updateProductVariant(
            @PathVariable String id,
            @Valid @RequestBody UpdateProductVariantRequest request
    ) {
        ProductVariantDto variant = updateProductVariantUseCase.execute(id, request);
        return ResponseEntity.ok(variant);
    }

    /**
     * Update product variant stock quantity.
     */
    @PutMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update variant stock", description = "Manually update product variant stock quantity (Admin only)")
    public ResponseEntity<ProductVariantDto> updateProductVariantStock(
            @PathVariable String id,
            @Valid @RequestBody UpdateProductVariantStockRequest request
    ) {
        ProductVariantDto variant = updateProductVariantStockUseCase.execute(id, request);
        return ResponseEntity.ok(variant);
    }

    /**
     * Delete a product variant.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Delete product variant", description = "Delete a product variant (Admin only)")
    public ResponseEntity<Void> deleteProductVariant(@PathVariable String id) {
        deleteProductVariantUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}

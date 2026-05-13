package com.bristol.api.controller;

import com.bristol.application.product.dto.ProductDto;
import com.bristol.application.product.dto.UpdateProductStockRequest;
import com.bristol.application.product.usecase.*;
import com.bristol.application.shared.dto.PagedResponse;
import com.bristol.domain.product.ProductCategory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for product catalog endpoints.
 * Creation and update of typed products (beer, merch, special) use their own typed controllers.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product catalog management endpoints")
public class ProductController {

    private final GetAllProductsUseCase getAllProductsUseCase;
    private final GetProductByIdUseCase getProductByIdUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final GetFeaturedProductsUseCase getFeaturedProductsUseCase;
    private final GetProductsByCategoryUseCase getProductsByCategoryUseCase;
    private final UpdateProductStockUseCase updateProductStockUseCase;
    private final GetAllProductsPaginatedUseCase getAllProductsPaginatedUseCase;
    private final GetProductsByCategoryPaginatedUseCase getProductsByCategoryPaginatedUseCase;
    private final GetFeaturedProductsPaginatedUseCase getFeaturedProductsPaginatedUseCase;
    private final SearchProductsPaginatedUseCase searchProductsPaginatedUseCase;

    @GetMapping
    @Operation(summary = "Get all products")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(getAllProductsUseCase.execute());
    }

    @GetMapping("/page")
    @Operation(summary = "Get all products (paginated)")
    public ResponseEntity<PagedResponse<ProductDto>> getAllProductsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(getAllProductsPaginatedUseCase.execute(page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ProductDto> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(getProductByIdUseCase.execute(id));
    }

    @GetMapping("/featured")
    @Operation(summary = "Get featured products")
    public ResponseEntity<List<ProductDto>> getFeaturedProducts() {
        return ResponseEntity.ok(getFeaturedProductsUseCase.execute());
    }

    @GetMapping("/featured/page")
    @Operation(summary = "Get featured products (paginated)")
    public ResponseEntity<PagedResponse<ProductDto>> getFeaturedProductsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(getFeaturedProductsPaginatedUseCase.execute(page, size));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get products by category")
    public ResponseEntity<List<ProductDto>> getProductsByCategory(@PathVariable ProductCategory category) {
        return ResponseEntity.ok(getProductsByCategoryUseCase.execute(category));
    }

    @GetMapping("/category/{category}/page")
    @Operation(summary = "Get products by category (paginated)")
    public ResponseEntity<PagedResponse<ProductDto>> getProductsByCategoryPaginated(
            @PathVariable ProductCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(getProductsByCategoryPaginatedUseCase.execute(category, page, size));
    }

    @GetMapping("/search")
    @Operation(summary = "Search products (paginated)")
    public ResponseEntity<PagedResponse<ProductDto>> searchProducts(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(searchProductsPaginatedUseCase.execute(q, page, size));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Delete product (soft delete)", description = "Admin only")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        deleteProductUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update product stock", description = "Admin only")
    public ResponseEntity<ProductDto> updateProductStock(
            @PathVariable String id,
            @Valid @RequestBody UpdateProductStockRequest request
    ) {
        return ResponseEntity.ok(updateProductStockUseCase.execute(id, request));
    }
}

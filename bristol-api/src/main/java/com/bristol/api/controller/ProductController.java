package com.bristol.api.controller;

import com.bristol.application.product.dto.CreateProductRequest;
import com.bristol.application.product.dto.ProductDto;
import com.bristol.application.product.dto.UpdateProductRequest;
import com.bristol.application.product.dto.UpdateProductStockRequest;
import com.bristol.application.product.usecase.*;
import com.bristol.application.shared.dto.PagedResponse;
import com.bristol.domain.product.ProductCategory;
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
 * REST controller for product endpoints.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product catalog management endpoints")
public class ProductController {

    private final GetAllProductsUseCase getAllProductsUseCase;
    private final GetProductByIdUseCase getProductByIdUseCase;
    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final GetFeaturedProductsUseCase getFeaturedProductsUseCase;
    private final GetProductsByCategoryUseCase getProductsByCategoryUseCase;
    private final UpdateProductStockUseCase updateProductStockUseCase;
    private final GetAllProductsPaginatedUseCase getAllProductsPaginatedUseCase;
    private final GetProductsByCategoryPaginatedUseCase getProductsByCategoryPaginatedUseCase;
    private final GetFeaturedProductsPaginatedUseCase getFeaturedProductsPaginatedUseCase;
    private final SearchProductsPaginatedUseCase searchProductsPaginatedUseCase;

    /**
     * Get all products.
     */
    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieve all products in the catalog")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<ProductDto> products = getAllProductsUseCase.execute();
        return ResponseEntity.ok(products);
    }

    /**
     * Get all products with pagination.
     */
    @GetMapping("/page")
    @Operation(summary = "Get all products (paginated)", description = "Retrieve all products with pagination")
    public ResponseEntity<PagedResponse<ProductDto>> getAllProductsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PagedResponse<ProductDto> products = getAllProductsPaginatedUseCase.execute(page, size);
        return ResponseEntity.ok(products);
    }

    /**
     * Get product by ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve a single product by its ID")
    public ResponseEntity<ProductDto> getProductById(@PathVariable String id) {
        ProductDto product = getProductByIdUseCase.execute(id);
        return ResponseEntity.ok(product);
    }

    /**
     * Get featured products.
     */
    @GetMapping("/featured")
    @Operation(summary = "Get featured products", description = "Retrieve all featured products")
    public ResponseEntity<List<ProductDto>> getFeaturedProducts() {
        List<ProductDto> products = getFeaturedProductsUseCase.execute();
        return ResponseEntity.ok(products);
    }

    /**
     * Get featured products with pagination.
     */
    @GetMapping("/featured/page")
    @Operation(summary = "Get featured products (paginated)", description = "Retrieve featured products with pagination")
    public ResponseEntity<PagedResponse<ProductDto>> getFeaturedProductsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PagedResponse<ProductDto> products = getFeaturedProductsPaginatedUseCase.execute(page, size);
        return ResponseEntity.ok(products);
    }

    /**
     * Get products by category.
     */
    @GetMapping("/category/{category}")
    @Operation(summary = "Get products by category", description = "Retrieve all products in a specific category")
    public ResponseEntity<List<ProductDto>> getProductsByCategory(@PathVariable ProductCategory category) {
        List<ProductDto> products = getProductsByCategoryUseCase.execute(category);
        return ResponseEntity.ok(products);
    }

    /**
     * Get products by category with pagination.
     */
    @GetMapping("/category/{category}/page")
    @Operation(summary = "Get products by category (paginated)", description = "Retrieve products by category with pagination")
    public ResponseEntity<PagedResponse<ProductDto>> getProductsByCategoryPaginated(
            @PathVariable ProductCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PagedResponse<ProductDto> products = getProductsByCategoryPaginatedUseCase.execute(category, page, size);
        return ResponseEntity.ok(products);
    }

    /**
     * Search products by name or description with pagination.
     */
    @GetMapping("/search")
    @Operation(summary = "Search products (paginated)", description = "Search products by name or description with pagination")
    public ResponseEntity<PagedResponse<ProductDto>> searchProducts(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PagedResponse<ProductDto> products = searchProductsPaginatedUseCase.execute(q, page, size);
        return ResponseEntity.ok(products);
    }

    /**
     * Create a new product.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Create product", description = "Create a new product (Admin only)")
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductDto product = createProductUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    /**
     * Update an existing product.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update product", description = "Update an existing product (Admin only)")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        ProductDto product = updateProductUseCase.execute(id, request);
        return ResponseEntity.ok(product);
    }

    /**
     * Delete a product (soft delete).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Delete product", description = "Soft delete a product (Admin only)")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        deleteProductUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Update product stock quantity.
     */
    @PutMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update product stock", description = "Manually update product stock quantity (Admin only)")
    public ResponseEntity<ProductDto> updateProductStock(
            @PathVariable String id,
            @Valid @RequestBody UpdateProductStockRequest request
    ) {
        ProductDto product = updateProductStockUseCase.execute(id, request);
        return ResponseEntity.ok(product);
    }
}

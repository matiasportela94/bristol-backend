package com.bristol.api.controller;

import com.bristol.application.product.beer.dto.BeerProductDto;
import com.bristol.application.product.beer.dto.CreateBeerProductRequest;
import com.bristol.application.product.beer.dto.UpdateBeerProductRequest;
import com.bristol.application.product.beer.dto.UpdateFullBeerProductRequest;
import com.bristol.application.product.beer.usecase.CreateBeerProductUseCase;
import com.bristol.application.product.beer.usecase.CreateFullBeerProductUseCase;
import com.bristol.application.product.beer.usecase.GetAllBeerProductsUseCase;
import com.bristol.application.product.beer.usecase.GetBeerProductByIdUseCase;
import com.bristol.application.product.beer.usecase.UpdateBeerProductUseCase;
import com.bristol.application.product.beer.usecase.UpdateFullBeerProductUseCase;
import com.bristol.application.product.beer.dto.CreateFullBeerProductRequest;
import com.bristol.application.product.dto.ProductImageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/products/beer")
@RequiredArgsConstructor
@Tag(name = "Beer Products", description = "Beer product management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class BeerProductController {

    private final CreateBeerProductUseCase createBeerProductUseCase;
    private final CreateFullBeerProductUseCase createFullBeerProductUseCase;
    private final UpdateFullBeerProductUseCase updateFullBeerProductUseCase;
    private final GetAllBeerProductsUseCase getAllBeerProductsUseCase;
    private final GetBeerProductByIdUseCase getBeerProductByIdUseCase;
    private final UpdateBeerProductUseCase updateBeerProductUseCase;
    private final ObjectMapper objectMapper;

    @GetMapping
    @Operation(summary = "Get all beer products")
    public ResponseEntity<List<BeerProductDto>> getAllBeerProducts() {
        return ResponseEntity.ok(getAllBeerProductsUseCase.execute());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get beer product by ID")
    public ResponseEntity<BeerProductDto> getBeerProductById(@PathVariable String id) {
        return ResponseEntity.ok(getBeerProductByIdUseCase.execute(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create beer product", description = "Admin only")
    public ResponseEntity<BeerProductDto> createBeerProduct(@Valid @RequestBody CreateBeerProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createBeerProductUseCase.execute(request));
    }

    @PostMapping(value = "/full", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create beer product with images and variants in one request", description = "Admin only. Send as multipart/form-data: 'data' = JSON (CreateFullBeerProductRequest), 'images' = image files (optional).")
    public ResponseEntity<BeerProductDto> createFullBeerProduct(
            @RequestPart("data") String dataJson,
            @RequestParam(value = "images", required = false) List<MultipartFile> imageFiles
    ) throws IOException {
        CreateFullBeerProductRequest request = objectMapper.readValue(dataJson, CreateFullBeerProductRequest.class);
        request.setImages(toImageRequests(imageFiles));
        return ResponseEntity.status(HttpStatus.CREATED).body(createFullBeerProductUseCase.execute(request));
    }

    @PutMapping(value = "/{id}/full", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update beer product with images and variants in one request", description = "Admin only. Send as multipart/form-data: 'data' = JSON (UpdateFullBeerProductRequest), 'images' = image files (optional, null keeps existing).")
    public ResponseEntity<BeerProductDto> updateFullBeerProduct(
            @PathVariable String id,
            @RequestPart("data") String dataJson,
            @RequestParam(value = "images", required = false) List<MultipartFile> imageFiles
    ) throws IOException {
        UpdateFullBeerProductRequest request = objectMapper.readValue(dataJson, UpdateFullBeerProductRequest.class);
        if (imageFiles != null) {
            request.setImages(toImageRequests(imageFiles));
        }
        return ResponseEntity.ok(updateFullBeerProductUseCase.execute(id, request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update beer product", description = "Admin only")
    public ResponseEntity<BeerProductDto> updateBeerProduct(
            @PathVariable String id,
            @Valid @RequestBody UpdateBeerProductRequest request) {
        return ResponseEntity.ok(updateBeerProductUseCase.execute(id, request));
    }

    private List<ProductImageRequest> toImageRequests(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return List.of();
        return IntStream.range(0, files.size()).mapToObj(i -> {
            try {
                MultipartFile f = files.get(i);
                return ProductImageRequest.builder()
                        .fileName(f.getOriginalFilename())
                        .contentType(f.getContentType())
                        .dataBase64(Base64.getEncoder().encodeToString(f.getBytes()))
                        .displayOrder(i)
                        .primary(i == 0)
                        .build();
            } catch (IOException e) {
                throw new RuntimeException("Failed to read image", e);
            }
        }).toList();
    }
}

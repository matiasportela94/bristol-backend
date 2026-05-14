package com.bristol.api.controller;

import com.bristol.application.product.dto.ProductImageRequest;
import com.bristol.application.product.special.dto.CreateFullSpecialProductRequest;
import com.bristol.application.product.special.dto.CreateSpecialProductRequest;
import com.bristol.application.product.special.dto.SpecialProductDto;
import com.bristol.application.product.special.dto.UpdateSpecialProductRequest;
import com.bristol.application.product.special.dto.UpdateFullSpecialProductRequest;
import com.bristol.application.product.special.usecase.CreateFullSpecialProductUseCase;
import com.bristol.application.product.special.usecase.CreateSpecialProductUseCase;
import com.bristol.application.product.special.usecase.GetAllSpecialProductsUseCase;
import com.bristol.application.product.special.usecase.GetSpecialProductByIdUseCase;
import com.bristol.application.product.special.usecase.UpdateFullSpecialProductUseCase;
import com.bristol.application.product.special.usecase.UpdateSpecialProductUseCase;
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
@RequestMapping("/api/products/special")
@RequiredArgsConstructor
@Tag(name = "Special Products", description = "Special product management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class SpecialProductController {

    private final CreateSpecialProductUseCase createSpecialProductUseCase;
    private final CreateFullSpecialProductUseCase createFullSpecialProductUseCase;
    private final UpdateFullSpecialProductUseCase updateFullSpecialProductUseCase;
    private final GetAllSpecialProductsUseCase getAllSpecialProductsUseCase;
    private final GetSpecialProductByIdUseCase getSpecialProductByIdUseCase;
    private final UpdateSpecialProductUseCase updateSpecialProductUseCase;
    private final ObjectMapper objectMapper;

    @GetMapping
    @Operation(summary = "Get all special products")
    public ResponseEntity<List<SpecialProductDto>> getAllSpecialProducts() {
        return ResponseEntity.ok(getAllSpecialProductsUseCase.execute());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get special product by ID")
    public ResponseEntity<SpecialProductDto> getSpecialProductById(@PathVariable String id) {
        return ResponseEntity.ok(getSpecialProductByIdUseCase.execute(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create special product", description = "Admin only")
    public ResponseEntity<SpecialProductDto> createSpecialProduct(@Valid @RequestBody CreateSpecialProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createSpecialProductUseCase.execute(request));
    }

    @PostMapping(value = "/full", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create special product with images and variants in one request", description = "Admin only. Send as multipart/form-data: 'data' = JSON (CreateFullSpecialProductRequest), 'images' = image files (optional).")
    public ResponseEntity<SpecialProductDto> createFullSpecialProduct(
            @RequestPart("data") String dataJson,
            @RequestParam(value = "images", required = false) List<MultipartFile> imageFiles
    ) throws IOException {
        CreateFullSpecialProductRequest request = objectMapper.readValue(dataJson, CreateFullSpecialProductRequest.class);
        request.setImages(toImageRequests(imageFiles));
        return ResponseEntity.status(HttpStatus.CREATED).body(createFullSpecialProductUseCase.execute(request));
    }

    @PutMapping(value = "/{id}/full", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update special product with images and variants in one request", description = "Admin only.")
    public ResponseEntity<SpecialProductDto> updateFullSpecialProduct(
            @PathVariable String id,
            @RequestPart("data") String dataJson,
            @RequestParam(value = "images", required = false) List<MultipartFile> imageFiles
    ) throws IOException {
        UpdateFullSpecialProductRequest request = objectMapper.readValue(dataJson, UpdateFullSpecialProductRequest.class);
        if (imageFiles != null) {
            request.setImages(toImageRequests(imageFiles));
        }
        return ResponseEntity.ok(updateFullSpecialProductUseCase.execute(id, request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update special product", description = "Admin only")
    public ResponseEntity<SpecialProductDto> updateSpecialProduct(
            @PathVariable String id,
            @Valid @RequestBody UpdateSpecialProductRequest request) {
        return ResponseEntity.ok(updateSpecialProductUseCase.execute(id, request));
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

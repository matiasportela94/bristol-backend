package com.bristol.api.controller;

import com.bristol.application.product.dto.ProductImageRequest;
import com.bristol.application.product.merch.dto.CreateFullMerchProductRequest;
import com.bristol.application.product.merch.dto.CreateMerchProductRequest;
import com.bristol.application.product.merch.dto.MerchProductDto;
import com.bristol.application.product.merch.dto.UpdateMerchProductRequest;
import com.bristol.application.product.merch.dto.UpdateFullMerchProductRequest;
import com.bristol.application.product.merch.usecase.CreateFullMerchProductUseCase;
import com.bristol.application.product.merch.usecase.CreateMerchProductUseCase;
import com.bristol.application.product.merch.usecase.GetAllMerchProductsUseCase;
import com.bristol.application.product.merch.usecase.GetMerchProductByIdUseCase;
import com.bristol.application.product.merch.usecase.UpdateFullMerchProductUseCase;
import com.bristol.application.product.merch.usecase.UpdateMerchProductUseCase;
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
@RequestMapping("/api/products/merch")
@RequiredArgsConstructor
@Tag(name = "Merch Products", description = "Merch product management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class MerchProductController {

    private final CreateMerchProductUseCase createMerchProductUseCase;
    private final CreateFullMerchProductUseCase createFullMerchProductUseCase;
    private final UpdateFullMerchProductUseCase updateFullMerchProductUseCase;
    private final GetAllMerchProductsUseCase getAllMerchProductsUseCase;
    private final GetMerchProductByIdUseCase getMerchProductByIdUseCase;
    private final UpdateMerchProductUseCase updateMerchProductUseCase;
    private final ObjectMapper objectMapper;

    @GetMapping
    @Operation(summary = "Get all merch products")
    public ResponseEntity<List<MerchProductDto>> getAllMerchProducts() {
        return ResponseEntity.ok(getAllMerchProductsUseCase.execute());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get merch product by ID")
    public ResponseEntity<MerchProductDto> getMerchProductById(@PathVariable String id) {
        return ResponseEntity.ok(getMerchProductByIdUseCase.execute(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create merch product", description = "Admin only")
    public ResponseEntity<MerchProductDto> createMerchProduct(@Valid @RequestBody CreateMerchProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createMerchProductUseCase.execute(request));
    }

    @PostMapping(value = "/full", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create merch product with images and variants in one request", description = "Admin only. Send as multipart/form-data: 'data' = JSON (CreateFullMerchProductRequest), 'images' = image files (optional).")
    public ResponseEntity<MerchProductDto> createFullMerchProduct(
            @RequestPart("data") String dataJson,
            @RequestParam(value = "images", required = false) List<MultipartFile> imageFiles
    ) throws IOException {
        CreateFullMerchProductRequest request = objectMapper.readValue(dataJson, CreateFullMerchProductRequest.class);
        request.setImages(toImageRequests(imageFiles));
        return ResponseEntity.status(HttpStatus.CREATED).body(createFullMerchProductUseCase.execute(request));
    }

    @PutMapping(value = "/{id}/full", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update merch product with images and variants in one request", description = "Admin only.")
    public ResponseEntity<MerchProductDto> updateFullMerchProduct(
            @PathVariable String id,
            @RequestPart("data") String dataJson,
            @RequestParam(value = "images", required = false) List<MultipartFile> imageFiles
    ) throws IOException {
        UpdateFullMerchProductRequest request = objectMapper.readValue(dataJson, UpdateFullMerchProductRequest.class);
        if (imageFiles != null) {
            request.setImages(toImageRequests(imageFiles));
        }
        return ResponseEntity.ok(updateFullMerchProductUseCase.execute(id, request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update merch product", description = "Admin only")
    public ResponseEntity<MerchProductDto> updateMerchProduct(
            @PathVariable String id,
            @Valid @RequestBody UpdateMerchProductRequest request) {
        return ResponseEntity.ok(updateMerchProductUseCase.execute(id, request));
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

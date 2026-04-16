package com.bristol.application.product.usecase;

import com.bristol.application.product.dto.ProductImageDto;
import com.bristol.application.product.dto.ProductImageRequest;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductImage;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;

/**
 * Maps product image payloads between API DTOs and domain objects.
 */
@Component
@RequiredArgsConstructor
public class ProductImagePayloadMapper {

    private final TimeProvider timeProvider;

    public List<ProductImage> toDomainList(ProductId productId, List<ProductImageRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return List.of();
        }

        int primaryIndex = resolvePrimaryIndex(requests);
        Instant now = timeProvider.now();
        List<ProductImage> images = new ArrayList<>(requests.size());

        for (int i = 0; i < requests.size(); i++) {
            ProductImageRequest request = requests.get(i);
            if (request == null) {
                throw new ValidationException("Image request cannot be null");
            }

            images.add(ProductImage.create(
                    productId,
                    decode(request.getDataBase64()),
                    request.getContentType(),
                    request.getFileName(),
                    request.getDisplayOrder() != null ? request.getDisplayOrder() : i,
                    i == primaryIndex,
                    now
            ));
        }

        return images;
    }

    public List<ProductImageDto> toDtoList(List<ProductImage> images) {
        if (images == null || images.isEmpty()) {
            return List.of();
        }

        return sort(images).stream()
                .map(this::toDto)
                .toList();
    }

    public String toPrimaryImageDataUrl(List<ProductImage> images) {
        if (images == null || images.isEmpty()) {
            return null;
        }

        ProductImage primary = sort(images).stream()
                .filter(ProductImage::isPrimary)
                .findFirst()
                .orElse(sort(images).get(0));

        return toDataUrl(primary);
    }

    private ProductImageDto toDto(ProductImage image) {
        return ProductImageDto.builder()
                .id(image.getId().getValue().toString())
                .fileName(image.getFileName())
                .contentType(image.getContentType())
                .dataBase64(Base64.getEncoder().encodeToString(image.getImageData()))
                .displayOrder(image.getDisplayOrder())
                .primary(image.isPrimary())
                .build();
    }

    private String toDataUrl(ProductImage image) {
        return "data:" + image.getContentType()
                + ";base64," + Base64.getEncoder().encodeToString(image.getImageData());
    }

    private byte[] decode(String dataBase64) {
        try {
            return Base64.getDecoder().decode(normalizeBase64Payload(dataBase64));
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Image data must be valid base64");
        }
    }

    private int resolvePrimaryIndex(List<ProductImageRequest> requests) {
        for (int i = 0; i < requests.size(); i++) {
            ProductImageRequest request = requests.get(i);
            if (request != null && Boolean.TRUE.equals(request.getPrimary())) {
                return i;
            }
        }
        return 0;
    }

    private List<ProductImage> sort(List<ProductImage> images) {
        return images.stream()
                .sorted(Comparator.comparing(ProductImage::getDisplayOrder)
                        .thenComparing(ProductImage::getCreatedAt))
                .toList();
    }

    private String normalizeBase64Payload(String dataBase64) {
        if (dataBase64 == null) {
            return null;
        }

        String trimmed = dataBase64.trim();
        int markerIndex = trimmed.indexOf(";base64,");
        if (trimmed.startsWith("data:") && markerIndex > 0) {
            return trimmed.substring(markerIndex + ";base64,".length());
        }

        return trimmed;
    }
}

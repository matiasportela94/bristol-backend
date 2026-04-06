package com.bristol.domain.product;

import com.bristol.domain.shared.exception.ValidationException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Arrays;

/**
 * ProductImage entity.
 * Represents an image stored directly in the database for a product.
 */
@Getter
@Builder(toBuilder = true)
public class ProductImage {

    private final ProductImageId id;
    private final ProductId productId;
    @Getter(AccessLevel.NONE)
    private final byte[] imageData;
    private final String contentType;
    private final String fileName;
    private final Integer displayOrder;
    private final boolean isPrimary;
    private final Instant createdAt;

    /**
     * Factory method to create a new product image.
     */
    public static ProductImage create(
            ProductId productId,
            byte[] imageData,
            String contentType,
            String fileName,
            Integer displayOrder,
            boolean isPrimary,
            Instant now
    ) {
        validateImageData(imageData);
        validateContentType(contentType);
        validateFileName(fileName);
        validateDisplayOrder(displayOrder);

        return ProductImage.builder()
                .id(ProductImageId.generate())
                .productId(productId)
                .imageData(Arrays.copyOf(imageData, imageData.length))
                .contentType(contentType.trim())
                .fileName(fileName != null ? fileName.trim() : null)
                .displayOrder(displayOrder != null ? displayOrder : 0)
                .isPrimary(isPrimary)
                .createdAt(now)
                .build();
    }

    public byte[] getImageData() {
        return imageData != null ? Arrays.copyOf(imageData, imageData.length) : null;
    }

    /**
     * Set as primary image.
     */
    public ProductImage setAsPrimary() {
        return this.toBuilder()
                .isPrimary(true)
                .build();
    }

    /**
     * Unset as primary image.
     */
    public ProductImage unsetAsPrimary() {
        return this.toBuilder()
                .isPrimary(false)
                .build();
    }

    /**
     * Update display order.
     */
    public ProductImage updateOrder(Integer newOrder) {
        validateDisplayOrder(newOrder);

        return this.toBuilder()
                .displayOrder(newOrder)
                .build();
    }

    private static void validateImageData(byte[] imageData) {
        if (imageData == null || imageData.length == 0) {
            throw new ValidationException("Image data is required");
        }
    }

    private static void validateContentType(String contentType) {
        if (contentType == null || contentType.trim().isEmpty()) {
            throw new ValidationException("Image content type is required");
        }
        if (!contentType.trim().toLowerCase().startsWith("image/")) {
            throw new ValidationException("Image content type must start with image/");
        }
    }

    private static void validateFileName(String fileName) {
        if (fileName != null && fileName.length() > 255) {
            throw new ValidationException("Image file name cannot exceed 255 characters");
        }
    }

    private static void validateDisplayOrder(Integer displayOrder) {
        if (displayOrder != null && displayOrder < 0) {
            throw new ValidationException("Image display order cannot be negative");
        }
    }
}

package com.bristol.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

/**
 * JPA Entity for product_images table.
 */
@Entity
@Table(name = "product_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "product_id", nullable = false, columnDefinition = "UUID")
    private UUID productId;

    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(name = "image_data", columnDefinition = "BYTEA")
    private byte[] imageData;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (isPrimary == null) {
            isPrimary = false;
        }
        if (displayOrder == null) {
            displayOrder = 0;
        }
        if (imageData != null) {
            imageData = Arrays.copyOf(imageData, imageData.length);
        }
    }
}

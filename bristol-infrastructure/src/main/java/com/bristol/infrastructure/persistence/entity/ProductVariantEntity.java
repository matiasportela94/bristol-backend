package com.bristol.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * JPA Entity for product_variants table.
 */
@Entity
@Table(name = "product_variants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "product_id", nullable = false, columnDefinition = "UUID")
    private UUID productId;

    @Column(unique = true, length = 100)
    private String sku;

    @Column(name = "variant_name", nullable = false, length = 100)
    private String variantName;

    @Column(name = "size_ml")
    private Integer sizeMl;

    @Column(length = 50)
    private String color;

    @Column(name = "additional_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal additionalPrice;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (isActive == null) {
            isActive = true;
        }
        if (stockQuantity == null) {
            stockQuantity = 0;
        }
        if (additionalPrice == null) {
            additionalPrice = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}

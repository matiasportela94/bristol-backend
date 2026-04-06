package com.bristol.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * JPA Entity for product_price_history table.
 */
@Entity
@Table(name = "product_price_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceHistoryEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "product_id", nullable = false, columnDefinition = "UUID")
    private UUID productId;

    @Column(name = "old_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal oldPrice;

    @Column(name = "new_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal newPrice;

    @Column(name = "changed_at", nullable = false, updatable = false)
    private Instant changedAt;

    @PrePersist
    protected void onCreate() {
        changedAt = Instant.now();
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
}

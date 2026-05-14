package com.bristol.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * JPA Entity for distributors table.
 */
@Entity
@Table(name = "distributors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DistributorEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true, columnDefinition = "UUID")
    private UUID userId;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(nullable = false, length = 50)
    private String cuit;

    @Column(name = "business_name", nullable = false, length = 255)
    private String businessName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DistributorStatusEnum status;

    @Column(name = "total_spent", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalSpent;

    @Column(name = "total_beers_purchased", nullable = false)
    private Integer totalBeersPurchased;

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
        if (status == null) {
            status = DistributorStatusEnum.PENDING;
        }
        if (totalSpent == null) {
            totalSpent = BigDecimal.ZERO;
        }
        if (totalBeersPurchased == null) {
            totalBeersPurchased = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public enum DistributorStatusEnum {
        PENDING, APPROVED, REJECTED
    }
}

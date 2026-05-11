package com.bristol.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA Entity for merch_types table.
 */
@Entity
@Table(name = "merch_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchTypeEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private MerchCategoryEnum category;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public enum MerchCategoryEnum {
        CLOTHING,
        GLASSWARE,
        ACCESSORIES,
        OTHER
    }
}

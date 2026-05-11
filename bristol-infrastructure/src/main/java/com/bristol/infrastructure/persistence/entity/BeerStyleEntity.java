package com.bristol.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA Entity for beer_styles table.
 */
@Entity
@Table(name = "beer_styles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerStyleEntity {

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
    private BeerStyleCategoryEnum category;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public enum BeerStyleCategoryEnum {
        ALE,
        LAGER,
        STOUT,
        WHEAT,
        SOUR,
        SPECIALTY
    }
}

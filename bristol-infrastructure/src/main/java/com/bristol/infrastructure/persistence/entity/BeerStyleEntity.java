package com.bristol.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(name = "image_data", columnDefinition = "BYTEA")
    private byte[] imageData;

    @Column(name = "image_content_type", length = 100)
    private String imageContentType;

    @Column(name = "image_file_name", length = 255)
    private String imageFileName;

    @Column(precision = 3, scale = 1)
    private java.math.BigDecimal abv;

    @Column
    private Integer ibu;

    @Column
    private Integer srm;

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

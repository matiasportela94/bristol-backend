package com.bristol.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * JPA Entity for products table.
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategoryEnum category;

    @Enumerated(EnumType.STRING)
    private ProductSubcategoryEnum subcategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "beer_type")
    private BeerTypeEnum beerType;

    @Enumerated(EnumType.STRING)
    @Column(name = "brewing_method")
    private BrewingMethodEnum brewingMethod;

    @Column(precision = 3, scale = 1)
    private BigDecimal abv;

    @Column(precision = 4, scale = 1)
    private BigDecimal ibu;

    @Column(precision = 4, scale = 1)
    private BigDecimal srm;

    @Enumerated(EnumType.STRING)
    private FlavorEnum flavor;

    @Enumerated(EnumType.STRING)
    private BitternessEnum bitterness;

    @Column(name = "base_price", precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "low_stock_threshold")
    private Integer lowStockThreshold;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured;

    @Column(name = "average_rating", precision = 2, scale = 1)
    private BigDecimal averageRating;

    @Column(name = "total_reviews", nullable = false)
    private Long totalReviews;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

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
        if (totalReviews == null) {
            totalReviews = 0L;
        }
        if (discountPercentage == null) {
            discountPercentage = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public enum ProductCategoryEnum {
        PRODUCTOS, MERCHANDISING, ESPECIALES
    }

    public enum ProductSubcategoryEnum {
        TWENTY_FOUR_PACK, SIX_PACK, CAN, KEG, GROWLER, REMERA, BUZO, GORRA, VASO, PLOTEO, EVENTO, OTRO
    }

    public enum BeerTypeEnum {
        IPA, LAGER, APA, STOUT, PORTER, PILSNER, SOUR, WHEAT, BLONDE, AMBER, GOLDEN, PALE_ALE, OTRO
    }

    public enum BrewingMethodEnum {
        TRADITIONAL, CRAFT, INDUSTRIAL
    }

    public enum FlavorEnum {
        FRUITY, MALTY, HOPPY, BITTER, SWEET, SOUR, SMOKY, SPICY
    }

    public enum BitternessEnum {
        LOW, MEDIUM, HIGH
    }
}

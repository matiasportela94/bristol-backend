package com.bristol.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * JPA Entity for beer products.
 * Stored in beer_products table (JOINED strategy).
 */
@Entity
@Table(name = "beer_products")
@DiscriminatorValue("BEER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BeerProductEntity extends BaseProductEntity {

    @Column(name = "beer_style_id", columnDefinition = "UUID")
    private UUID beerStyleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "beer_category", length = 50)
    private BeerCategoryEnum beerCategory;

    @Column(precision = 3, scale = 1)
    private BigDecimal abv;

    @Column(precision = 4, scale = 0)
    private Integer ibu;

    @Column(precision = 4, scale = 0)
    private Integer srm;

    @Column(length = 100)
    private String origin;

    @Column(length = 100)
    private String brewery;

    public enum BeerCategoryEnum {
        ALE, LAGER, STOUT, WHEAT, SOUR, SPECIALTY
    }
}

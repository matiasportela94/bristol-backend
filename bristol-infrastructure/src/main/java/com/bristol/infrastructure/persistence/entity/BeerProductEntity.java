package com.bristol.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

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

    @Column(length = 100)
    private String origin;

    @Column(length = 100)
    private String brewery;

    @Column(name = "cans_per_unit")
    private Integer cansPerUnit;
}

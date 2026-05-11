package com.bristol.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * JPA Entity for merch products.
 * Stored in merch_products table (JOINED strategy).
 */
@Entity
@Table(name = "merch_products")
@DiscriminatorValue("MERCH")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MerchProductEntity extends BaseProductEntity {

    @Column(name = "merch_type_id", columnDefinition = "UUID")
    private UUID merchTypeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "merch_category", length = 50)
    private MerchCategoryEnum merchCategory;

    @Column(length = 100)
    private String material;

    @Column(length = 100)
    private String brand;

    public enum MerchCategoryEnum {
        CLOTHING, GLASSWARE, ACCESSORIES, OTHER
    }
}

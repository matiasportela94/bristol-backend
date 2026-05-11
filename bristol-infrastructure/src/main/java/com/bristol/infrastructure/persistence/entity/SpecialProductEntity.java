package com.bristol.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * JPA Entity for special products.
 * Stored in special_products table (JOINED strategy).
 */
@Entity
@Table(name = "special_products")
@DiscriminatorValue("SPECIAL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SpecialProductEntity extends BaseProductEntity {

    @Column(name = "special_type_id", columnDefinition = "UUID")
    private UUID specialTypeId;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "requires_quote", nullable = false)
    private Boolean requiresQuote;

    @PrePersist
    protected void onCreateSpecial() {
        super.onCreate();
        if (requiresQuote == null) {
            requiresQuote = false;
        }
    }
}

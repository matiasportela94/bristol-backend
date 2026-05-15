package com.bristol.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "brewery_inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BreweryInventoryEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "beer_style_id", columnDefinition = "UUID", nullable = false, unique = true)
    private UUID beerStyleId;

    @Column(name = "total_cans", nullable = false)
    private int totalCans;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}

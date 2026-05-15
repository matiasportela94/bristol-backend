package com.bristol.infrastructure.persistence.entity;

import com.bristol.domain.brewery.MovementType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "brewery_inventory_movements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BreweryInventoryMovementEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "beer_style_id", columnDefinition = "UUID", nullable = false)
    private UUID beerStyleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private MovementType type;

    @Column(name = "cans_delta", nullable = false)
    private int cansDelta;

    @Column(name = "cans_before", nullable = false)
    private int cansBefore;

    @Column(name = "cans_after", nullable = false)
    private int cansAfter;

    @Column(name = "reference_id", columnDefinition = "UUID")
    private UUID referenceId;

    @Column(name = "reference_type", length = 20)
    private String referenceType;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}

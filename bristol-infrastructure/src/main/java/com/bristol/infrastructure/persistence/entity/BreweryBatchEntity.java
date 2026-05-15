package com.bristol.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "brewery_batches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BreweryBatchEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "beer_style_id", columnDefinition = "UUID", nullable = false)
    private UUID beerStyleId;

    @Column(name = "cans_produced", nullable = false)
    private int cansProduced;

    @Column(name = "can_capacity_ml")
    private Integer canCapacityMl;

    @Column(name = "cost_per_can", precision = 10, scale = 2)
    private BigDecimal costPerCan;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}

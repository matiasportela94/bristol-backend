package com.bristol.application.brewery.dto;

import com.bristol.domain.brewery.MovementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BreweryInventoryMovementDto {
    private String id;
    private String beerStyleId;
    private String beerStyleName;
    private String beerStyleCode;
    private MovementType type;
    private int cansDelta;
    private int cansBefore;
    private int cansAfter;
    private UUID referenceId;
    private String referenceType;
    private String notes;
    private Instant createdAt;
}
